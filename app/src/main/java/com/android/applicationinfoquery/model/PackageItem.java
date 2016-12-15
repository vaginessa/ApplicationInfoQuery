package com.android.applicationinfoquery.model;

import android.graphics.drawable.Drawable;

public class PackageItem {
    private Drawable mIcon;
    private String mName;
    private String mPackageName;
    private String mMainActivity;
    private boolean mIsSystemApp;

    public PackageItem(Drawable icon, String name, String packageName, String mainActivity, boolean systemApp) {
        mIcon = icon;
        mName = name;
        mPackageName = packageName;
        mMainActivity = mainActivity;
        mIsSystemApp = systemApp;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public String getName() {
        return mName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getMainActivity() {
        return mMainActivity;
    }

    public boolean isSystemApp() {
        return mIsSystemApp;
    }

    @Override
    public String toString() {
        return "[" + mIcon + ", " + mName + ", " + ", " + mPackageName + ", " + mMainActivity + "," + mIsSystemApp + "]";
    }
}
