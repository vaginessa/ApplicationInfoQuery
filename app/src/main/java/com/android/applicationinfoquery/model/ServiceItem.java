package com.android.applicationinfoquery.model;

import android.graphics.drawable.Drawable;

/**
 * Created by user on 16-12-17.
 */

public class ServiceItem {

    private Drawable mIcon;
    private String mName;
    private String mPackageName;
    private String mClassName;

    public ServiceItem(Drawable icon, String name, String packageName, String className) {
        mIcon = icon;
        mName = name;
        mPackageName = packageName;
        mClassName = className;
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

    public String getClassName() {
        return mClassName;
    }

    @Override
    public String toString() {
        return "[" + mIcon + ", " + mName + ", " + mPackageName + ", " + mClassName + "]";
    }
}
