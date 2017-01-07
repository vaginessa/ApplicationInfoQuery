package com.android.aiq;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final String EXTRA_TYPE = "type";
    public static final int TYPE_ALL_APPLICATION = 0;
    public static final int TYPE_SYSTEM_APPLICATION = 1;
    public static final int TYPE_NON_SYSTEM_APPLICATION = 2;

    public static Dialog getLoadProgressDialog(Context context, int resId) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.ProgressDialogTheme);
        dialog.setMessage(context.getString(resId));
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static ArrayList<ListItem> getApplicationList(Context context, int type) {
        ArrayList<ListItem> list = new ArrayList<ListItem>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infos = pm.getInstalledPackages(0);
        if (infos != null) {
            PackageInfo pi = null;
            ActivityInfo ai = null;
            Drawable icon = null;
            String name = null;
            String packageName = null;
            String launcherActivity = null;
            boolean isSystemApp = false;
            boolean hasLauncherActivity = false;
            for (int i = 0; i < infos.size(); i++) {
                pi = infos.get(i);
                icon = pi.applicationInfo.loadIcon(pm);
                if (icon == null) {
                    icon = context.getDrawable(R.mipmap.ic_launcher);
                }
                CharSequence cName = pi.applicationInfo.loadLabel(pm);
                if (TextUtils.isEmpty(cName)) {
                    name = context.getString(R.string.unknown_applicatin_name);
                } else {
                    name = cName.toString();
                }
                packageName = pi.packageName;
                ai = getLauncherActivityInfo(context, packageName);
                if (ai != null) {
                    launcherActivity = ai.name;
                    hasLauncherActivity = true;
                } else {
                    launcherActivity = context.getString(R.string.not_found_launcher_activity);
                    hasLauncherActivity = false;
                }
                isSystemApp = isSystemApp(pi) || isSystemUpdateApp(pi);
                ListItem item = new ListItem(icon, name, packageName, launcherActivity, isSystemApp, hasLauncherActivity);
                switch (type) {
                    case TYPE_ALL_APPLICATION:
                        list.add(item);
                        break;

                    case TYPE_SYSTEM_APPLICATION:
                        if (isSystemApp) {
                            list.add(item);
                        }
                        break;

                    case TYPE_NON_SYSTEM_APPLICATION:
                        if (!isSystemApp) {
                            list.add(item);
                        }
                        break;
                }
            }
        }
        return list;
    }

    private static ActivityInfo getLauncherActivityInfo(Context context, String packageName) {
        ActivityInfo ai = null;
        PackageManager pm = context.getPackageManager();
        Intent launcher = new Intent();
        launcher.setAction(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);
        launcher.setPackage(packageName);
        List<ResolveInfo> list = pm.queryIntentActivities(launcher, PackageManager.MATCH_ALL);
        if (list != null && list.size() > 0) {
            if (list.get(0).activityInfo != null) {
                ai = list.get(0).activityInfo;
            }
        }
        return ai;
    }

    public static boolean isSystemApp(PackageInfo info) {
        return ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static boolean isSystemUpdateApp(PackageInfo info) {
        return ((info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }
}
