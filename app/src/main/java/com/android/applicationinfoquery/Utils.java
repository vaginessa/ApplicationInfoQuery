package com.android.applicationinfoquery;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.android.applicationinfoquery.model.PackageItem;
import com.android.applicationinfoquery.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/16.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static final String EXTRA_PACKAGE_TYPE = "package_type";
    public static final String TYPE_ALL = "all";
    public static final String TYPE_SYSTEM = "system";
    public static final String TYPE_NON_SYSTEM = "non_system";

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
            String name = null;
            String packageName = null;
            String mainActivity = null;
            for (PackageInfo info : mAllPackages) {

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

    public boolean isSystemApp(PackageInfo info) {
        return ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public boolean isSystemUpdateApp(PackageInfo info) {
        return ((info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

}
