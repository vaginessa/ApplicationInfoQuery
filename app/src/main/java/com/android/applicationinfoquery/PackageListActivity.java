package com.android.applicationinfoquery;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.applicationinfoquery.model.PackageItem;
import com.android.applicationinfoquery.model.Type;

import java.util.ArrayList;

public class PackageListActivity extends ListActivity {

    private ListView mListView;
    private TextView mEmptyView;
    private PackageListAdapter mAdapter;
    private ArrayList<PackageItem> mList;
    private Type mType = Type.UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = getListView();
        mEmptyView = (TextView) getLayoutInflater().inflate(R.layout.empty_list_view, mListView, false);
        mListView.setEmptyView(mEmptyView);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mType = getType(intent);
        if (mType == Type.UNKNOWN) {
            finish();
        } else {
            mList = Utils.getPackageItems(this, mType);
            mAdapter = new PackageListAdapter(this, mList);
            mListView.setAdapter(mAdapter);
        }
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
                holder.mainActivity = (TextView) view.findViewById(R.id.application_main_activity);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            PackageItem item = mList.get(i);
            holder.icon.setImageDrawable(item.getIcon());
            holder.name.setText(item.getName());
            holder.packageName.setText(item.getPackageName());
            holder.mainActivity.setText(item.getMainActivity());
            return view;
        }

        class ViewHolder {
            ImageView icon;
            TextView name;
            TextView packageName;
            TextView mainActivity;
        }
    }

}
