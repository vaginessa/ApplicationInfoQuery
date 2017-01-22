package com.android.aiq;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PackageInfoActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private TextView mEmptyView;
    private PackageInfoAdapter mAdapter;
    private LoadAsyncTask mLoadTask;
    private Dialog mLoadDialog;
    private ArrayList<InfoItem> mList;
    private PackageInfo mPackageInfo;

    private int mMode;
    private String mPackageName;
    private String mApkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initValues();
        initViews();
        onNewIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_info_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mMode == Utils.USER_MODE) {
            menu.findItem(R.id.mode).setTitle(getString(R.string.developer_mode));
        } else if (mMode == Utils.DEVELOPER_MODE) {
            menu.findItem(R.id.mode).setTitle(getString(R.string.user_mode));
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.mode:
                if (mMode == Utils.USER_MODE) {
                    mMode = Utils.DEVELOPER_MODE;
                } else if (mMode == Utils.DEVELOPER_MODE) {
                    mMode = Utils.USER_MODE;
                }
                updateViews(mMode);
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            mPackageName = intent.getStringExtra(Utils.EXTRA_PACKAGE_NAME);
            mApkPath = intent.getStringExtra(Utils.EXTRA_APK_PATH);
        }

        if (!TextUtils.isEmpty(mPackageName)) {
            mPackageInfo = Utils.getPackageInfoByPackageName(this, mPackageName, 0);
        } else if (!TextUtils.isEmpty(mApkPath)) {
            mPackageInfo = Utils.getPackageInfoByPath(this, mApkPath, 0);
        }

        if (mPackageInfo != null) {
            getActionBar().setTitle(getString(R.string.package_info_title, Utils.getApplicationLabel(this, mPackageInfo)));
            updateViews(mMode);
        } else {
            finish();
        }
    }

    private void initValues() {
        mMode = Utils.USER_MODE;
        mPackageName = null;
        mApkPath = null;
        mPackageInfo = null;
    }

    private void initViews() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mEmptyView = Utils.getEmptyView(this, R.string.no_package_info);
        ((ViewGroup) getListView().getParent()).addView(mEmptyView);
        getListView().setEmptyView(mEmptyView);
        getListView().setOnItemClickListener(this);
    }

    private void updateViews(int mode) {
        mList = new ArrayList<InfoItem>();
        mAdapter = new PackageInfoAdapter(this, mList);
        getListView().setAdapter(mAdapter);
        if (mLoadTask != null) {
            if (mLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
                mLoadTask.cancel(true);
            }
            mLoadTask = null;
        }
        mLoadTask = new LoadAsyncTask(this, mPackageInfo, mode);
        mLoadTask.execute(new Void[]{});
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InfoItem item = mAdapter.getItem(position);
        if (item.action != Utils.ACTION_NONE) {
            if (item.action == Utils.ACTION_ACTIVITY) {
                Utils.startActivityByAction(this, item.type, mPackageName, mApkPath, mMode);
            } else if (item.action == Utils.ACTION_DIALOG) {
                Dialog dialog = Utils.createInfoDialog(this, item.type, mPackageName, mApkPath, mMode);
                if (dialog != null) {
                    dialog.show();
                }
            }
        }
    }

    /**
     * 加载信息列表异步线程
     */
    private class LoadAsyncTask extends AsyncTask<Void, Integer, Boolean> {

        private Context mContext;
        private PackageInfo mPackageInfo;
        private int mMode;

        public LoadAsyncTask(Context context, PackageInfo info, int mode) {
            mContext = context;
            mPackageInfo = info;
            mMode = mode;
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
            mList = Utils.getPackageInfoList(mContext, mPackageInfo, mMode);
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
            mAdapter.setList(mList);
        }
    }

    private class PackageInfoAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private ArrayList<InfoItem> mList;

        public PackageInfoAdapter(Context context, ArrayList<InfoItem> list) {
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            mList = list;
        }

        public void setList(ArrayList<InfoItem> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public InfoItem getItem(int position) {
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
                convertView = mInflater.inflate(R.layout.info_item, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.info = (TextView) convertView.findViewById(R.id.info);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            InfoItem item = mList.get(position);
            holder.title.setText(item.titleId);
            holder.info.setText(item.info);
            return convertView;
        }

        class ViewHolder {
            TextView title;
            TextView info;
        }
    }
}
