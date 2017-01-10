package com.android.aiq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import static com.android.aiq.R.id.service_info;

public class ApplicationInfoList extends Activity implements View.OnClickListener {

    private String mPackageName;

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
        }
        if (!TextUtils.isEmpty(mPackageName)) {
            String label = Utils.getApplicationLabel(this, mPackageName);
            getActionBar().setTitle(label);
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
        if (launcherIntent != null) {
            menu.findItem(R.id.start_application).setEnabled(true);
        } else {
            menu.findItem(R.id.start_application).setEnabled(false);
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
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.package_info:

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

}
