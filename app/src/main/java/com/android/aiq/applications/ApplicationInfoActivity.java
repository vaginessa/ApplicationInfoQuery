package com.android.aiq.applications;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.aiq.R;
import com.android.aiq.utils.ApplicationUtils;
import com.android.aiq.utils.Utils;
import com.android.aiq.model.InfoListItem;

import java.util.ArrayList;

public class ApplicationInfoActivity extends ListActivity {

    private ListView mListView;
    private TextView mEmptyView;
    private ApplicationInfoAdapter mAdapter;
    private ArrayList<InfoListItem> mList;

    private String mPackageName;
    private String mPackageLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = getListView();
        mEmptyView = (TextView) getLayoutInflater().inflate(R.layout.empty_list_view, null);
        mEmptyView.setText(R.string.no_application_info_empty_text);
        ((ViewGroup)mListView.getParent()).addView(mEmptyView);
        mListView.setEmptyView(mEmptyView);
        mListView.setBackground(null);
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
        }

        if (!TextUtils.isEmpty(mPackageName) && !TextUtils.isEmpty(mPackageLabel)) {
            setTitle(getString(R.string.application_info_activity_title, mPackageLabel));
            mList = ApplicationUtils.getApplicationInfos(this, mPackageName);
            mAdapter = new ApplicationInfoAdapter(this, mList);
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

    private class ApplicationInfoAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private ArrayList<InfoListItem> mList;

        public ApplicationInfoAdapter(Context context, ArrayList<InfoListItem> list) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mList = list;
        }

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public InfoListItem getItem(int position) {
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
                convertView = mInflater.inflate(R.layout.application_info_list_view, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.application_info_title);
                holder.info = (TextView) convertView.findViewById(R.id.application_info_info);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            InfoListItem item = mList.get(position);
            holder.title.setText(item.getTitle());
            holder.info.setText(item.getInfo());
            return convertView;
        }

        class ViewHolder {
            TextView title;
            TextView info;
        }
    }
}
