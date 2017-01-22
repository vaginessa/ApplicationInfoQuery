package com.android.aiq;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.aiq.activitys.ActivityQueryActivity;
import com.android.aiq.broadcasts.BroadcastQueryActivity;
import com.android.aiq.providers.ProviderQueryActivity;
import com.android.aiq.service.ServiceQueryActivity;

public class ApplicationInfoQuery extends Activity implements View.OnClickListener {

    private MenuItem mShowTopActivityMenu;
    private IShowTopActivityService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_info_query);

        initViews();
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bindShowTopActivityService();
        //}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            unbindService(mConnection);
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            return super.onCreateOptionsMenu(menu);
//        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_application_info_query, menu);
        mShowTopActivityMenu = menu.findItem(R.id.real_time_display);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            return super.onPrepareOptionsMenu(menu);
//        }
        if (mService != null) {
            try {
                if (mService.isShowing()) {
                    mShowTopActivityMenu.setTitle(R.string.hide_top_activity);
                } else {
                    mShowTopActivityMenu.setTitle(R.string.show_top_activity);
                }
            } catch (RemoteException e) {
                Log.e(this, "onPrepareOptionsMenu=>error: ", e);
                mShowTopActivityMenu.setEnabled(false);
            }
        } else {
            mShowTopActivityMenu.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            return super.onMenuItemSelected(featureId, item);
//        }
        if (mService != null) {
            try {
                if (mService.isShowing()) {
                    mService.cancelRealTimeInfo();
                } else {
                    mService.showRealTimeInfo();
                }
            } catch (RemoteException e) {
                Log.e(this, "onMenuItemSelected=>error: ", e);
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all_application_info:
                Intent all = new Intent(this, ApplicationListActivity.class);
                all.putExtra(Utils.EXTRA_TYPE, Utils.TYPE_ALL_APPLICATION);
                startActivity(all);
                break;

            case R.id.system_application_info:
                Intent system = new Intent(this, ApplicationListActivity.class);
                system.putExtra(Utils.EXTRA_TYPE, Utils.TYPE_SYSTEM_APPLICATION);
                startActivity(system);
                break;

            case R.id.non_system_application_info:
                Intent nonsystem = new Intent(this, ApplicationListActivity.class);
                nonsystem.putExtra(Utils.EXTRA_TYPE, Utils.TYPE_NON_SYSTEM_APPLICATION);
                startActivity(nonsystem);
                break;

            case R.id.uninstall_application_info:
                Intent ua = new Intent(this, UninstallApplication.class);
                startActivity(ua);
                break;

            case R.id.package_query:
                Intent packageQuery = new Intent(this, PackageQueryActivity.class);
                startActivity(packageQuery);
                break;

            case R.id.activity_query:
                Intent activityQuery = new Intent(this, ActivityQueryActivity.class);
                startActivity(activityQuery);
                break;

            case R.id.service_query:
                Intent serviceQuery = new Intent(this, ServiceQueryActivity.class);
                startActivity(serviceQuery);
                break;

            case R.id.broadcast_query:
                Intent broadcastQuery = new Intent(this, BroadcastQueryActivity.class);
                startActivity(broadcastQuery);
                break;

            case R.id.provider_query:
                Intent providerQuery = new Intent(this, ProviderQueryActivity.class);
                startActivity(providerQuery);
                break;
        }
    }

    private void initViews() {
        Button allApplicationInfoBt = (Button) findViewById(R.id.all_application_info);
        Button systemApplicationInfoBt = (Button) findViewById(R.id.system_application_info);
        Button nonSystemApplicationInfoBt = (Button) findViewById(R.id.non_system_application_info);
        Button uninstallApplicationInfoBt = (Button) findViewById(R.id.uninstall_application_info);
        Button packageQueryBt = (Button) findViewById(R.id.package_query);
        Button activityQueryBt = (Button) findViewById(R.id.activity_query);
        Button serviceQueryBt = (Button) findViewById(R.id.service_query);
        Button broadcastQueryBt = (Button) findViewById(R.id.broadcast_query);
        Button providerQueryBt = (Button) findViewById(R.id.provider_query);

        allApplicationInfoBt.setOnClickListener(this);
        systemApplicationInfoBt.setOnClickListener(this);
        nonSystemApplicationInfoBt.setOnClickListener(this);
        uninstallApplicationInfoBt.setOnClickListener(this);
        packageQueryBt.setOnClickListener(this);
        activityQueryBt.setOnClickListener(this);
        serviceQueryBt.setOnClickListener(this);
        broadcastQueryBt.setOnClickListener(this);
        providerQueryBt.setOnClickListener(this);
    }

    private void bindShowTopActivityService() {
        Intent service = new Intent(this, ShowTopActivityService.class);
        bindService(service, mConnection, Service.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = IShowTopActivityService.Stub.asInterface(iBinder);
            Log.d("ServiceConnection", "onServiceConnected=>service: " + mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("ServiceConnection", "onServiceDisconnected()");
            mService = null;
        }
    };
}
