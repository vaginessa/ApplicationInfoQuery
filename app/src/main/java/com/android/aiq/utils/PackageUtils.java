package com.android.aiq.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.android.aiq.R;
import com.android.aiq.model.PackageItem;
import com.android.aiq.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16-12-19.
 */

public class PackageUtils {

    public static final String TAG = "PackageUtils";

    public static ArrayList<PackageItem> getPackageItems(Context context, Type type) {
        ArrayList<PackageItem> list = new ArrayList<PackageItem>();
        if (type == Type.ALL) {
            list = getAllPackages(context);
        } else if (type == Type.SYSTEM) {
            list = getSystemPackages(context);
        } else if (type == Type.NONSYSTEM) {
            list = getNonSystemPackages(context);
        }
        return list;
    }

    public static ArrayList<PackageItem> getAllPackages(Context context) {
        ArrayList<PackageItem> list = new ArrayList<PackageItem>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> mAllPackages = new ArrayList<PackageInfo>();
        mAllPackages = pm.getInstalledPackages(0);
        if (mAllPackages != null) {
            Drawable icon = null;
            int uid = context.getApplicationInfo().uid;
            String name = null;
            String packageName = null;
            String launcherActivity = null;
            boolean hasLauncherActivity = false;
            boolean isSystemApp = false;
            for (PackageInfo info : mAllPackages) {
                icon = info.applicationInfo.loadIcon(pm);
                uid = info.applicationInfo.uid;
                if (icon == null) {
                    icon = info.applicationInfo.loadLogo(pm);
                    if (icon == null) {
                        icon = context.getDrawable(R.mipmap.ic_launcher);
                    }
                }
                CharSequence nameCh = info.applicationInfo.loadLabel(pm);
                if (nameCh != null) {
                    name = nameCh.toString();
                    if (TextUtils.isEmpty(name)) {
                        name = context.getString(R.string.unknown_name);
                    }
                }
                packageName = info.packageName;
                launcherActivity = Utils.getLauncherActivity(context, packageName);
                if (launcherActivity.equals(context.getString(R.string.not_find_launcher_activity))) {
                    hasLauncherActivity = false;
                } else {
                    hasLauncherActivity = true;
                }
                isSystemApp = Utils.isSystemApp(info) || Utils.isSystemUpdateApp(info);
                PackageItem item = new PackageItem(icon, uid, name, packageName,
                        launcherActivity, hasLauncherActivity, isSystemApp);
                list.add(item);
            }
        }
        return list;
    }

    public static ArrayList<PackageItem> getSystemPackages(Context context) {
        ArrayList<PackageItem> list = new ArrayList<PackageItem>();
        ArrayList<PackageItem> allList = getAllPackages(context);
        for (PackageItem item : allList) {
            if (item.isSystemApp()) {
                list.add(item);
            }
        }
        return list;
    }

    public static ArrayList<PackageItem> getNonSystemPackages(Context context) {
        ArrayList<PackageItem> list = new ArrayList<PackageItem>();
        ArrayList<PackageItem> allList = getAllPackages(context);
        for (PackageItem item : allList) {
            if (!item.isSystemApp()) {
                list.add(item);
            }
        }
        return list;
    }
}
