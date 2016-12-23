package com.android.aiq.providers;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.aiq.R;
import com.android.aiq.service.ServiceInfoActivity;
import com.android.aiq.utils.ProviderUtils;
import com.android.aiq.utils.Utils;
import com.android.aiq.model.ListItem;

import java.util.ArrayList;

public class ProvidersListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private PackageManager mPackageManager;
    private ListView mListView;
    private TextView mEmptyView;
    private ProviderListAdapter mAdapter;
    private ArrayList<ListItem> mList;

    private String mPackageName;
    private String mPackageLabel;
    private Intent mQueryIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mPackageManager = getPackageManager();
        mListView = getListView();
        mEmptyView = (TextView) getLayoutInflater().inflate(R.layout.empty_list_view, mListView, false);
        mEmptyView.setText(R.string.provider_list_empty_text);
        ((ViewGroup)getListView().getParent()).addView(mEmptyView);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(mEmptyView);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mPackageName = null;
        mPackageLabel = null;
        if (intent != null) {
            mPackageName = intent.getStringExtra(Utils.EXTRA_PACKAGE_NAME);
            mPackageLabel = intent.getStringExtra(Utils.EXTRA_APPLICATION_LABEL);
            mQueryIntent = intent.getParcelableExtra(Utils.EXTRA_INTENT);
        }

        if (!TextUtils.isEmpty(mPackageName) && !TextUtils.isEmpty(mPackageLabel)) {
            setTitle(getString(R.string.provider_list_title, mPackageLabel));
            mList = ProviderUtils.getProvidersList(this, mPackageName);
            mAdapter = new ProviderListAdapter(this, mList);
            mListView.setAdapter(mAdapter);
        } else if (mQueryIntent != null) {
            setTitle(R.string.provider_query_result_title);
            mList = ProviderUtils.getProvidersListByIntent(this, mQueryIntent);
            mAdapter = new ProviderListAdapter(this, mList);
            mListView.setAdapter(mAdapter);
        } else {
            finish();
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListItem item = mAdapter.getItem(position);
        Intent provider = new Intent(this, ProviderInfoActivity.class);
        provider.putExtra(Utils.EXTRA_PACKAGE_NAME, item.getPackageName());
        provider.putExtra(Utils.EXTRA_APPLICATION_LABEL, item.getName());
        provider.putExtra(Utils.EXTRA_CLASS_NAME, item.getClassName());
        startActivity(provider);
    }

    private class ProviderListAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private ArrayList<ListItem> mList;

        public ProviderListAdapter(Context context, ArrayList<ListItem> list) {
            mContext = context;
            mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            mList = list;
        }

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
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
            ListItem item = mList.get(i);
            holder.icon.setImageDrawable(item.getIcon());
            holder.name.setText(item.getName());
            holder.packageName.setText(item.getPackageName());
            holder.launcherActivity.setText(item.getClassName());
            return view;
        }

        class ViewHolder {
            ImageView icon;
            TextView name;
            TextView packageName;
            TextView launcherActivity;
        }
    }
}
