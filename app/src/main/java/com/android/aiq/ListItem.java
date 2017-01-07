package com.android.aiq;

import android.graphics.drawable.Drawable;

public class ListItem {

    public Drawable icon;
    public String name;
    public String packageName;
    public String launcherActivity;
    boolean isSystemApp;
    boolean hasLauncherActivity;

    public ListItem(Drawable icon, String name, String packageName,
                    String launcherActivity, boolean isSystemApp, boolean hasLauncherActivity) {
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;
        this.launcherActivity = launcherActivity;
        this.isSystemApp = isSystemApp;
        this.hasLauncherActivity = hasLauncherActivity;
    }

    @Override
    public String toString() {
        return "[" + icon + ", " + name + ", " + packageName + ", " + launcherActivity + ", " + isSystemApp + ", " + hasLauncherActivity + "]";
    }
}
