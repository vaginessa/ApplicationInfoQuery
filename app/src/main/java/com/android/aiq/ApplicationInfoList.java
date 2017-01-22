package com.android.aiq;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ApplicationInfoList extends Activity implements View.OnClickListener {

    private PackageInfo mPackageInfo;
    private Dialog mLoadDialog;
    private CopyAsyncTask mCopyTask;

    private String mPackageName;
    private String mApkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_info_list);

        initValues();
        initViews();
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            mPackageName = intent.getStringExtra(Utils.EXTRA_PACKAGE_NAME);
            mApkPath = intent.getStringExtra(Utils.EXTRA_APK_PATH);
            Log.d(this, "onNewIntent=>packageName: " + mPackageName + " path: " + mApkPath);
        }
        if (!TextUtils.isEmpty(mPackageName)) {
            mPackageInfo = Utils.getPackageInfoByPackageName(this, mPackageName, 0);
            String label = Utils.getApplicationLabel(this, mPackageName);
            getActionBar().setTitle(label);
        } else if (!TextUtils.isEmpty(mApkPath)) {
            mPackageInfo = Utils.getPackageInfoByPath(this, mApkPath, 0);
            getActionBar().setTitle(mPackageInfo.applicationInfo.loadLabel(getPackageManager()));
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_application_info_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Intent launcherIntent = Utils.getLauncherIntent(this, mPackageName);
        if (!TextUtils.isEmpty(mApkPath)) {
            menu.findItem(R.id.install_application).setVisible(true);
            menu.findItem(R.id.start_application).setVisible(false);
            menu.findItem(R.id.uninstall_application).setVisible(false);
            menu.findItem(R.id.copy_application).setVisible(false);
        } else {
            menu.findItem(R.id.install_application).setVisible(false);
            menu.findItem(R.id.copy_application).setVisible(true);
            if (launcherIntent != null) {
                menu.findItem(R.id.start_application).setVisible(true);
            } else {
                menu.findItem(R.id.start_application).setVisible(false);
            }
            if (mPackageInfo != null) {
                if (Utils.isSystemApp(mPackageInfo) || Utils.isSystemUpdateApp(mPackageInfo)) {
                    menu.findItem(R.id.uninstall_application).setVisible(false);
                } else {
                    menu.findItem(R.id.uninstall_application).setVisible(true);
                }
            } else {
                menu.findItem(R.id.uninstall_application).setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.start_application:
                Intent launcherIntent = Utils.getLauncherIntent(this, mPackageName);
                launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launcherIntent);
                return true;

            case R.id.uninstall_application:
                Utils.uninstallApplication(this, mPackageName);
                return true;

            case R.id.install_application:
                Utils.installApk(this, mApkPath);
                return true;

            case R.id.copy_application:
                if (mCopyTask != null) {
                    if (mCopyTask.getStatus() == AsyncTask.Status.RUNNING) {
                        mCopyTask.cancel(true);
                    }
                    mCopyTask = null;
                }
                mCopyTask = new CopyAsyncTask(this, mPackageName);
                mCopyTask.execute(new Void[]{});
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.package_info:
                Intent packageInfo = new Intent(this, PackageInfoActivity.class);
                if (!TextUtils.isEmpty(mPackageName)) {
                    packageInfo.putExtra(Utils.EXTRA_PACKAGE_NAME, mPackageName);
                } else if (!TextUtils.isEmpty(mApkPath)) {
                    packageInfo.putExtra(Utils.EXTRA_APK_PATH, mApkPath);
                }
                startActivity(packageInfo);
                break;

            case R.id.application_info:

                break;

            case R.id.activity_info:

                break;

            case R.id.service_info:

                break;

            case R.id.broadcast_info:

                break;

            case R.id.provider_info:

                break;
        }
    }

    private void initValues() {
        mApkPath = null;
        mPackageName = null;
    }

    private void initViews() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Button mPackageInfoBt = (Button) findViewById(R.id.package_info);
        Button mApplicationInfoBt = (Button) findViewById(R.id.application_info);
        Button mActivityInfoBt = (Button) findViewById(R.id.activity_info);
        Button mServiceInfoBt = (Button) findViewById(R.id.service_info);
        Button mBroadcastInfoBt = (Button) findViewById(R.id.broadcast_info);
        Button mProviderInfoBt = (Button) findViewById(R.id.provider_info);

        mPackageInfoBt.setOnClickListener(this);
        mApplicationInfoBt.setOnClickListener(this);
        mActivityInfoBt.setOnClickListener(this);
        mServiceInfoBt.setOnClickListener(this);
        mBroadcastInfoBt.setOnClickListener(this);
        mProviderInfoBt.setOnClickListener(this);
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

}
