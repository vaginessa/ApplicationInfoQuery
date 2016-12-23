package com.android.aiq.model;

import android.graphics.drawable.Drawable;

/**
 * Created by user on 16-12-19.
 */

public class PackageItem {

    private Drawable mIcon;
    private int mUid;
    private String mName;
    private String mPackageName;
    private String mLauncherActivity;
    private boolean mHasLauncherActivity;
    private boolean mIsSystemApp;

    public PackageItem(Drawable icon, int uid, String name, String packageName,
                       String launcherActivity, boolean hasLauncherActivity, boolean isSystemApp) {
        mIcon = icon;
        mUid = uid;
        mName = name;
        mPackageName = packageName;
        mLauncherActivity = launcherActivity;
        mHasLauncherActivity = hasLauncherActivity;
        mIsSystemApp = isSystemApp;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public int getUid() {
        return mUid;
    }

    public String getName() {
        return mName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getLauncherActivity() {
        return mLauncherActivity;
    }

    public boolean hasLauncherActivity() {
        return mHasLauncherActivity;
    }

    public boolean isSystemApp() {
        return mIsSystemApp;
    }

    @Override
    public String toString() {
        return "[" + mIcon + ", " + mUid + ", " + mName + ", " + mPackageName
                + ", " + mLauncherActivity + ", " + mHasLauncherActivity + ", " + mIsSystemApp + "]";
    }
}
