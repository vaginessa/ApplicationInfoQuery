package com.android.aiq;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ApplicationListActivity extends ListActivity {

    private TextView mEmptyView;
    private Dialog mLoadDialog;
    private ApplicationListAdapter mAdapter;
    private LoadAsyncTask mLoadTask;
    private ArrayList<ListItem> mList;

    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initValues();
        initViews();
        onNewIntent(getIntent());
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
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void initValues() {
        mType = -1;
    }

    private void initViews() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mEmptyView = getEmptyView();
        ((ViewGroup)getListView().getParent()).addView(mEmptyView);
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
            mLoadDialog = Utils.getLoadProgressDialog(mContext, R.string.load_msg);
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
                    Intent intent = new Intent();
                    ComponentName name = new ComponentName(item.packageName, item.launcherActivity);
                    intent.setComponent(name);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
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
