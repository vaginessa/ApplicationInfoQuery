package com.android.applicationinfoquery;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class RealTimeDisplayService extends Service {

    private static final int NOTIFY_ID = 8;
    private static final int MSG_REFRESH = 0;
    private static final String EXTRA_SHOW_INFO = "show_info";

    private ActivityManager mActivityManager;
    private PackageManager mPackageManager;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private TextView mInfoTv;

    private int mBindCount = 0;
    private int mRefreshTime;
    private boolean mIsShowing;

    @Override
    public IBinder onBind(Intent intent) {
        mBindCount++;
        Log.d(this, "onBind=>count: " + mBindCount);
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mBindCount--;
        Log.d(this, "onUnbind=>count: " + mBindCount);
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mPackageManager = getPackageManager();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mRefreshTime = getResources().getInteger(R.integer.real_time_display_refresh_time);
        mIsShowing = false;
        initFloatWindow();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean showInfo = intent.getBooleanExtra(EXTRA_SHOW_INFO, false);
        if (showInfo) {
            showFloatWindow();
        } else {
            removeFloatWindow();
            if (mBindCount <= 0) {
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    private void initFloatWindow() {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mInfoTv = (TextView) inflater.inflate(R.layout.real_time_info_view, null);
    }

    private void showFloatWindow() {
        Log.d(this, "showFloatWindow=>showing: " + mIsShowing);
        if (!mIsShowing) {
            mInfoTv.setText(getTopActivityInfo());
            mWindowManager.addView(mInfoTv, mLayoutParams);
            startForeground();
            mIsShowing = true;
            mHandler.sendEmptyMessageDelayed(MSG_REFRESH,mRefreshTime * 1000);
        }
    }

    private void removeFloatWindow() {
        Log.d(this, "removeFloatWindow=>showing: " + mIsShowing);
        if (mIsShowing) {
            mHandler.removeMessages(MSG_REFRESH);
            mWindowManager.removeView(mInfoTv);
            mIsShowing = false;
            stopForeground(true);
        }
    }

    private void updateFloatWindow() {
        Log.d(this, "updateFloatWindow=>showing: " + mIsShowing);
        if (mIsShowing) {
            mInfoTv.setText(getTopActivityInfo());
            mWindowManager.updateViewLayout(mInfoTv, mLayoutParams);
        }
    }

    private void startForeground() {
        Notification status = new Notification();
        status.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(NOTIFY_ID, status);
    }

    private String getTopActivityInfo() {
        StringBuilder info = new StringBuilder();
        ComponentName cn = getTopActivityComponentName();
        Log.d(this, "getTopActivityInfo=>cn: " + cn);
        if (cn != null && !TextUtils.isEmpty(cn.getPackageName())) {
            ApplicationInfo ai = getTopActivityApplicationInfo(cn.getPackageName());
            String appName = ai.loadLabel(mPackageManager).toString();
            info.append(getString(R.string.top_application_name, appName));
            info.append("\n");
            info.append(getString(R.string.top_application_packagename, cn.getClassName()));
            info.append("\n");
            info.append(getString(R.string.top_application_classname, cn.getClassName()));
        }
        Log.d(this, "getTopActivityInfo=>info: " + info.toString());
        Log.d(this, "getTopActivityInfo=>packageName: " + getTopActivityInfo());
        return info.toString();
    }

    private ComponentName getTopActivityComponentName() {
        return mActivityManager.getRunningTasks(1).get(0).topActivity;
    }

    private ApplicationInfo getTopActivityApplicationInfo(String packageName) {
        ApplicationInfo info = null;
        try {
            info = mPackageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

    private String getTopActivity() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usm = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST,ts-2000, ts);

        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }

        UsageStats recentStats = null;
        for (UsageStats usageStats : queryUsageStats) {
            if(recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()){
                recentStats = usageStats;
            }
        }
        return recentStats.getPackageName();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH:
                    if (mIsShowing) {
                        updateFloatWindow();
                        mHandler.sendEmptyMessageDelayed(MSG_REFRESH, mRefreshTime * 1000);
                    }
                    break;
            }
        }
    };

    private class MyBinder extends IRealTimeDisplayService.Stub {

        @Override
        public boolean isShowing() throws RemoteException {
            return mIsShowing;
        }

        @Override
        public void showRealTimeInfo() throws RemoteException {
            showFloatWindow();
        }

        @Override
        public void cancelRealTimeInfo() throws RemoteException {
            removeFloatWindow();
        }

    }
}
