package com.android.applicationinfoquery;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ApplicationInfoQuery extends Activity implements View.OnClickListener {

    private Button mAllApplicationInfoBt;
    private Button mSystemApplicationInfoBt;
    private Button mNonSystemApplicationInfoBt;
    private Button mActivityQueryBt;
    private Button mServiceQueryBt;
    private Button mBroadcastQueryBt;
    private MenuItem mRealTimeDisplayMenu;
    private IRealTimeDisplayService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_info_query);

        initViews();
        bindRealTimeDisplayService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.application_info_query_menu, menu);
        mRealTimeDisplayMenu = menu.findItem(R.id.real_time_display);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mService != null) {
            try {
                if (mService.isShowing()) {
                    mRealTimeDisplayMenu.setTitle(R.string.turn_off_real_time_info);
                } else {
                    mRealTimeDisplayMenu.setTitle(R.string.displays_real_time_info);
                }
            } catch (RemoteException e) {
                Log.e(this, "onPrepareOptionsMenu=>error: ", e);
                mRealTimeDisplayMenu.setEnabled(false);
            }
        } else {
            mRealTimeDisplayMenu.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
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

                break;

            case R.id.system_application_info:

                break;

            case R.id.non_system_application_info:

                break;

            case R.id.activity_query:

                break;

            case R.id.service_query:

                break;

            case R.id.broadcast_query:

                break;
        }
    }

    private void initViews() {
        mAllApplicationInfoBt = (Button) findViewById(R.id.all_application_info);
        mSystemApplicationInfoBt = (Button) findViewById(R.id.system_application_info);
        mNonSystemApplicationInfoBt = (Button) findViewById(R.id.non_system_application_info);
        mActivityQueryBt = (Button) findViewById(R.id.activity_query);
        mServiceQueryBt = (Button) findViewById(R.id.service_query);
        mBroadcastQueryBt = (Button) findViewById(R.id.broadcast_query);

        mAllApplicationInfoBt.setOnClickListener(this);
        mSystemApplicationInfoBt.setOnClickListener(this);
        mNonSystemApplicationInfoBt.setOnClickListener(this);
        mActivityQueryBt.setOnClickListener(this);
        mServiceQueryBt.setOnClickListener(this);
        mBroadcastQueryBt.setOnClickListener(this);
    }

    private void bindRealTimeDisplayService() {
        Intent service = new Intent(this, RealTimeDisplayService.class);
        bindService(service, mConnection, Service.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = IRealTimeDisplayService.Stub.asInterface(iBinder);
            Log.d("ServiceConnection", "onServiceConnected=>service: " + mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("ServiceConnection", "onServiceDisconnected()");
            mService = null;
        }
    };
}
