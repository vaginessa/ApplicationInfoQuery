package com.android.aiq.packages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.aiq.R;
import com.android.aiq.providers.ProvidersListActivity;
import com.android.aiq.utils.Utils;
import com.android.aiq.activitys.ActivitiesListActivity;
import com.android.aiq.applications.ApplicationInfoActivity;
import com.android.aiq.broadcasts.BroadcastsListActivity;
import com.android.aiq.service.ServicesListActivity;

public class PackageInfoActivity extends Activity implements View.OnClickListener {

    private Button mApplicationInfoBt;
    private Button mActivitysInfoBt;
    private Button mServicesInfoBt;
    private Button mBroadcastsInfoBt;
    private Button mProvidersInfoBt;

    private String mPackageName;
    private String mPackageLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_info);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
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
        if (!TextUtils.isEmpty(mPackageName) && ! TextUtils.isEmpty(mPackageLabel)) {
            setTitle(getString(R.string.package_info_title, mPackageLabel));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.applications_info:
                Intent application = new Intent(this, ApplicationInfoActivity.class);
                application.putExtra(Utils.EXTRA_PACKAGE_NAME, mPackageName);
                application.putExtra(Utils.EXTRA_APPLICATION_LABEL, mPackageLabel);
                startActivity(application);
                break;

            case R.id.activities_info:
                Intent activity = new Intent(this, ActivitiesListActivity.class);
                activity.putExtra(Utils.EXTRA_PACKAGE_NAME, mPackageName);
                activity.putExtra(Utils.EXTRA_APPLICATION_LABEL, mPackageLabel);
                startActivity(activity);
                break;

            case R.id.services_info:
                Intent service = new Intent(this, ServicesListActivity.class);
                service.putExtra(Utils.EXTRA_PACKAGE_NAME, mPackageName);
                service.putExtra(Utils.EXTRA_APPLICATION_LABEL, mPackageLabel);
                startActivity(service);
                break;

            case R.id.broadcasts_info:
                Intent broadcast = new Intent(this, BroadcastsListActivity.class);
                broadcast.putExtra(Utils.EXTRA_PACKAGE_NAME, mPackageName);
                broadcast.putExtra(Utils.EXTRA_APPLICATION_LABEL, mPackageLabel);
                startActivity(broadcast);
                break;

            case R.id.providers_info:
                Intent provider = new Intent(this, ProvidersListActivity.class);
                provider.putExtra(Utils.EXTRA_PACKAGE_NAME, mPackageName);
                provider.putExtra(Utils.EXTRA_APPLICATION_LABEL, mPackageLabel);
                startActivity(provider);
                break;
        }
    }

    private void initViews() {
        mApplicationInfoBt = (Button) findViewById(R.id.applications_info);
        mActivitysInfoBt = (Button) findViewById(R.id.activities_info);
        mServicesInfoBt = (Button) findViewById(R.id.services_info);
        mBroadcastsInfoBt = (Button) findViewById(R.id.broadcasts_info);
        mProvidersInfoBt = (Button) findViewById(R.id.providers_info);

        mApplicationInfoBt.setOnClickListener(this);
        mActivitysInfoBt.setOnClickListener(this);
        mServicesInfoBt.setOnClickListener(this);
        mBroadcastsInfoBt.setOnClickListener(this);
        mProvidersInfoBt.setOnClickListener(this);
    }

}
