package com.android.aiq.packages;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.aiq.Log;
import com.android.aiq.R;
import com.android.aiq.utils.PackageUtils;
import com.android.aiq.utils.Utils;
import com.android.aiq.model.PackageItem;
import com.android.aiq.model.Type;

import java.util.ArrayList;

/**
 * Bug list:
 *  （１）加载数据时未使用线程，在数据多时可能会出现无响应现象
 *  （２）停止应用功能未实现
 *  （３）清除数据功能未实现
 *  （４）清除缓存功能未实现
 */

public class PackageListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private static final int OP_SUCCESSFUL = 1;
    private static final int OP_FAILED = 2;
    private static final int MSG_CLEAR_USER_DATA = 1;
    private static final int MSG_CLEAR_CACHE = 3;

    private static final int START_APPLICATION_ID = Menu.FIRST + 1;
    private static final int STOP_APPLICATION_ID = Menu.FIRST + 2;
    private static final int CLEAR_APPLICATION_DATA_ID = Menu.FIRST + 3;
    private static final int CLEAR_APPLICATION_CACHE_ID = Menu.FIRST + 4;

    private ListView mListView;
    private TextView mEmptyView;
    private PackageListAdapter mAdapter;
    private ActivityManager mActivityManager;
    private PackageManager mPackageManager;
    private ArrayList<PackageItem> mList;
    private Type mType = Type.UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mPackageManager = getPackageManager();
        mListView = getListView();
        mEmptyView = (TextView) getLayoutInflater().inflate(R.layout.empty_list_view, mListView, false);
        mEmptyView.setText(R.string.package_list_empty_text);
        ((ViewGroup)getListView().getParent()).addView(mEmptyView);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(mEmptyView);
        registerForContextMenu(mListView);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mType = getType(intent);
        if (mType == Type.UNKNOWN) {
            finish();
        } else {
            mList = PackageUtils.getPackageItems(this, mType);
            mAdapter = new PackageListAdapter(this, mList);
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        PackageItem item = mAdapter.getItem(info.position);
        menu.setHeaderTitle(item.getName());
        menu.setHeaderIcon(item.getIcon());
        if (item.hasLauncherActivity()) {
            menu.add(0, START_APPLICATION_ID, 0, R.string.start_application);
        }
        menu.add(0, STOP_APPLICATION_ID, 1, R.string.stop_application);
        menu.add(0, CLEAR_APPLICATION_DATA_ID, 2, R.string.clear_application_data);
        menu.add(0, CLEAR_APPLICATION_CACHE_ID, 3, R.string.clear_application_cache);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        PackageItem packageItem = mAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case START_APPLICATION_ID:
                Log.d(this, "onContextItemSelected=>start application.");
                Intent application = new Intent();
                ComponentName cn = new ComponentName(packageItem.getPackageName(), packageItem.getLauncherActivity());
                application.setComponent(cn);
                application.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(application);
                break;

            case STOP_APPLICATION_ID:
                Log.d(this, "onContextItemSelected=>stop application.");
                Utils.forceStopApplication(this, packageItem);
                break;

            case CLEAR_APPLICATION_DATA_ID:
                Log.d(this, "onContextItemSelected=>clear application data.");

                break;

            case CLEAR_APPLICATION_CACHE_ID:
                Log.d(this, "onContextItemSelected=>clear application cache.");
                break;
        }
        return true;
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PackageItem item = mAdapter.getItem(position);
        Intent packageInfo = new Intent(this, PackageInfoActivity.class);
        packageInfo.putExtra(Utils.EXTRA_PACKAGE_NAME, item.getPackageName());
        packageInfo.putExtra(Utils.EXTRA_APPLICATION_LABEL, item.getName());
        startActivity(packageInfo);
    }

    private Type getType(Intent intent) {
        Type type = Type.UNKNOWN;
        if (intent != null) {
            String extra = intent.getStringExtra(Utils.EXTRA_PACKAGE_TYPE);
            if (Utils.TYPE_ALL.equals(extra)) {
                type = Type.ALL;
                setTitle(R.string.all_application_title);
            } else if (Utils.TYPE_SYSTEM.equals(extra)) {
                type = Type.SYSTEM;
                setTitle(R.string.system_application_title);
            } else if (Utils.TYPE_NON_SYSTEM.equals(extra)) {
                type = Type.NONSYSTEM;
                setTitle(R.string.non_system_application_title);
            }
        }
        Log.d(this, "getType=>type: " + type);
        return type;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CLEAR_USER_DATA:
                    if (msg.arg1 == OP_SUCCESSFUL) {

                    } else {

                    }
                    break;

                case MSG_CLEAR_CACHE:
                    if (msg.arg1 == OP_SUCCESSFUL) {

                    } else {

                    }
                    break;
            }
        }
    };

    private class PackageListAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private ArrayList<PackageItem> mList;

        public PackageListAdapter(Context context, ArrayList<PackageItem> list) {
            mContext = context;
            mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            mList = list;
        }

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public PackageItem getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = mInflater.inflate(R.layout.package_list_adapter_view, viewGroup, false);
                holder = new ViewHolder();
                holder.icon = (ImageView) view.findViewById(R.id.application_icon);
                holder.name = (TextView) view.findViewById(R.id.application_label);
                holder.packageName = (TextView) view.findViewById(R.id.application_package_name);
                holder.launcherActivity = (TextView) view.findViewById(R.id.application_launcher_activity);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            PackageItem item = mList.get(i);
            holder.icon.setImageDrawable(item.getIcon());
            holder.name.setText(item.getName());
            holder.packageName.setText(item.getPackageName());
            holder.launcherActivity.setText(item.getLauncherActivity());
            return view;
        }

        class ViewHolder {
            ImageView icon;
            TextView name;
            TextView packageName;
            TextView launcherActivity;
        }
    }

    class ClearCacheObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            final Message msg = mHandler.obtainMessage(MSG_CLEAR_CACHE);
            msg.arg1 = succeeded ? OP_SUCCESSFUL : OP_FAILED;
            mHandler.sendMessage(msg);
        }
    }

    class ClearUserDataObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            final Message msg = mHandler.obtainMessage(MSG_CLEAR_USER_DATA);
            msg.arg1 = succeeded ? OP_SUCCESSFUL : OP_FAILED;
            mHandler.sendMessage(msg);
        }
    }

}
