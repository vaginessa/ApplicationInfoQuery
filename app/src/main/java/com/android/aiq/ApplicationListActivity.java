package com.android.aiq;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.aiq.packages.PackageInfoActivity;

import java.util.ArrayList;

/**
 * ApplicationListActivity处理流程
 *
 * （１）通过启动Intent中附带的Utils.EXTRA_TYPE值，获取所有应用（Utils.TYPE_ALL_APPLICATION）、系统应用（Utils.TYPE_SYSTEM_APPLICATION）、非系统应用（Utils.TYPE_NON_SYSTEM_APPLICATION）
 * （２）由于获取应用时可能需要稍长时间，因此使用一个异步线程（LoadAsyncTask）来获取应用列表
 * （３）在获取应用列表成功前显示一个圆形进度对话框，提示用户正在加载应用列表，避免界面长时间没有变化，造成应用无反应或没有获取到应用的错觉。
 * （４）添加ListView上下文菜单，增加启动应用、禁用或启用应用（由于需要系统权限，因此该菜单会一直处于不可用状态）、卸载应用、拷贝apk
 * （５）由于用户可能想直接启动应用，因此在列表条目的右边添加一个启动应用的按钮，方便用户快速启动应用。
 */
public class ApplicationListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private TextView mEmptyView;
    private Dialog mLoadDialog;
    private ApplicationListAdapter mAdapter;
    private LoadAsyncTask mLoadTask;
    private CopyAsyncTask mCopyTask;
    private ArrayList<ListItem> mList;

    private int mType;
    private boolean mNeedUpdateViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initValues();
        initViews();
        // 统一在onNewIntent处理要显示的内容
        onNewIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当卸载软件后，需要更新应用列表
        if (mNeedUpdateViews) {
            mNeedUpdateViews = false;
            updateViews(mType);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(this, "onNewIntent=>intent: " + intent);
        if (intent != null) {
            mType =  intent.getIntExtra(Utils.EXTRA_TYPE, -1);
        }
        if (mType != -1) {
            updateViews(mType);
        } else {
            finish();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ListItem item = mAdapter.getItem(info.position);
        menu.setHeaderIcon(item.icon);
        menu.setHeaderTitle(item.name);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_application_list, menu);
        MenuItem start = menu.findItem(R.id.start_application);
        MenuItem enabledOrDisabled = menu.findItem(R.id.disabled_or_enabled_application);
        MenuItem uninstall = menu.findItem(R.id.uninstall_application);
        if (!item.hasLauncherActivity) {
            start.setEnabled(false);
        }
        if (item.isSystemApp) {
            uninstall.setEnabled(false);
        }

        //因为禁用和启用应用功能需要系统权限，所以默认禁用该功能并隐藏该菜单项
        /*
        if (Utils.isApplicationEnabled(this, item.packageName)) {
            enabledOrDisabled.setTitle(R.string.disabled_application);
        } else {
            enabledOrDisabled.setTitle(R.string.enabled_application);
        }
        */
        enabledOrDisabled.setVisible(false);
        enabledOrDisabled.setEnabled(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ListItem li = mAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.start_application:
                Utils.startActivity(this, li.packageName, li.launcherActivity);
                return true;

            case R.id.disabled_or_enabled_application:
                Utils.setApplicationEnabled(this, li.packageName, !Utils.isApplicationEnabled(this, li.packageName));
                return true;

            case R.id.uninstall_application:
                Utils.uninstallApplication(this, li.packageName);
                mNeedUpdateViews = true;
                return true;

            case R.id.copy_application:
                if (mCopyTask != null) {
                    if (mCopyTask.getStatus() == AsyncTask.Status.RUNNING) {
                        mCopyTask.cancel(true);
                    }
                    mCopyTask = null;
                }
                mCopyTask = new CopyAsyncTask(this, li.packageName);
                mCopyTask.execute(new Void[]{});
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListItem item = mAdapter.getItem(position);
        Intent applicationInfoList = new Intent(this, ApplicationInfoList.class);
        applicationInfoList.putExtra(Utils.EXTRA_PACKAGE_NAME, item.packageName);
        startActivity(applicationInfoList);
    }

    private void initValues() {
        mType = -1;
    }

    private void initViews() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mEmptyView = getEmptyView();
        ((ViewGroup)getListView().getParent()).addView(mEmptyView);
        registerForContextMenu(getListView());
        getListView().setOnItemClickListener(this);
    }

    private TextView getEmptyView() {
        TextView emptyView = new TextView(this);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.empty_view_text_size));
        emptyView.setText(R.string.application_not_found);
        emptyView.setVisibility(View.GONE);
        return emptyView;
    }

    private void updateViews(int mType) {
        switch (mType) {
            case Utils.TYPE_ALL_APPLICATION:
                getActionBar().setTitle(R.string.all_application);
                break;

            case Utils.TYPE_SYSTEM_APPLICATION:
                getActionBar().setTitle(R.string.system_application);
                break;

            case Utils.TYPE_NON_SYSTEM_APPLICATION:
                getActionBar().setTitle(R.string.non_system_application);
                break;

            default:
                finish();
                return;
        }
        mList = new ArrayList<ListItem>();
        mAdapter = new ApplicationListAdapter(this, mList);
        getListView().setAdapter(mAdapter);
        if (mLoadTask != null) {
            if (mLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
                mLoadTask.cancel(true);
            }
            mLoadTask = null;
        }
        mLoadTask = new LoadAsyncTask(this, mType);
        mLoadTask.execute(new Void[]{});
    }

    /**
     * 加载应用列表异步线程
     */
    private class LoadAsyncTask extends AsyncTask<Void, Integer, Boolean> {

        private Context mContext;
        private int mType;

        public LoadAsyncTask(Context context, int type) {
            mContext = context;
            mType = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mLoadDialog != null) {
                if (mLoadDialog.isShowing()) {
                    mLoadDialog.cancel();
                }
                mLoadDialog = null;
            }
            mLoadDialog = Utils.createLoadProgressDialog(mContext, R.string.load_msg);
            mLoadDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mList = Utils.getApplicationList(mContext, mType);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (mLoadDialog != null) {
                if (mLoadDialog.isShowing()) {
                    mLoadDialog.cancel();
                }
                mLoadDialog = null;
            }
            Toast.makeText(mContext, getString(R.string.found_application_toast_msg, mList.size()), Toast.LENGTH_SHORT).show();
            mAdapter.setList(mList);
        }
    }

    /**
     * 拷贝异步线程
     */
    private class CopyAsyncTask extends AsyncTask<Void, Integer, Boolean> {

        private Context mContext;
        private String mPackageName;

        public CopyAsyncTask(Context context, String packageName) {
            mContext = context;
            mPackageName = packageName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mLoadDialog != null) {
                if (mLoadDialog.isShowing()) {
                    mLoadDialog.cancel();
                }
                mLoadDialog = null;
            }
            mLoadDialog = Utils.createLoadProgressDialog(mContext, R.string.copy_msg);
            mLoadDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return Utils.copyApplication(mContext, mPackageName);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (mLoadDialog != null) {
                if (mLoadDialog.isShowing()) {
                    mLoadDialog.cancel();
                }
                mLoadDialog = null;
            }
            if (aBoolean) {
                Toast.makeText(mContext, mContext.getString(R.string.copy_application_msg,
                        Utils.getApplicationFileCopyPath(mContext, mPackageName)), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, R.string.copy_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ApplicationListAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private ArrayList<ListItem> mList;

        public ApplicationListAdapter(Context context, ArrayList<ListItem> list) {
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mList = list;
        }

        public void setList(ArrayList<ListItem> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public ListItem getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.application_list_item, parent, false);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.packageName = (TextView) convertView.findViewById(R.id.package_name);
                holder.launcherActivity = (TextView) convertView.findViewById(R.id.launcher_activity);
                holder.open = (Button) convertView.findViewById(R.id.open);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ListItem item = mList.get(position);
            holder.icon.setImageDrawable(item.icon);
            holder.name.setText(item.name);
            holder.packageName.setText(item.packageName);
            holder.launcherActivity.setText(item.launcherActivity);
            holder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.startActivity(mContext, item.packageName, item.launcherActivity);
                }
            });
            if (item.hasLauncherActivity) {
                holder.open.setEnabled(true);
            } else {
                holder.open.setEnabled(false);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView name;
            TextView packageName;
            TextView launcherActivity;
            Button open;
        }
    }
}
