package com.android.aiq.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AppSecurityPermissions;

import com.android.aiq.Log;
import com.android.aiq.R;
import com.android.aiq.model.InfoListItem;
import com.android.aiq.model.ListItem;
import com.android.aiq.model.PackageItem;
import com.android.aiq.model.Type;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/16.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static final String EXTRA_PACKAGE_TYPE = "package_type";
    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_APPLICATION_LABEL = "application_label";
    public static final String EXTRA_CLASS_NAME = "class_name";
    public static final String EXTRA_INTENT = "intent";

    public static final String TYPE_ALL = "all";
    public static final String TYPE_SYSTEM = "system";
    public static final String TYPE_NON_SYSTEM = "non_system";


    public static String getLauncherActivity(Context context, String packageName) {
        StringBuilder sb = new StringBuilder();
        PackageManager pm = context.getPackageManager();
        Intent queryIntent = new Intent();
        queryIntent.setPackage(packageName);
        queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(queryIntent, 0);
        if (list != null) {
            ResolveInfo info = null;
            for (int i = 0; i < list.size(); i++) {
                info = list.get(i);
                if (i > 0) {
                    sb.append("\n");
                }
                sb.append(info.activityInfo.name);
            }
        }
        if (sb.length() <= 0) {
            sb.append(context.getString(R.string.not_find_launcher_activity));
        }
        Log.d(TAG, "getLauncherActivity=>className: " + sb.toString());
        return sb.toString();
    }

    public static boolean isSystemApp(PackageInfo info) {
        return ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static boolean isSystemUpdateApp(PackageInfo info) {
        return ((info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    public static String getInstallLocation(Context context, int installLocation) {
        String location = context.getString(R.string.unknown_install_location);
        if (PackageInfo.INSTALL_LOCATION_AUTO == installLocation) {
            location = context.getString(R.string.install_location_auto);
        } else if (PackageInfo.INSTALL_LOCATION_INTERNAL_ONLY == installLocation) {
            location = context.getString(R.string.install_location_internal_only);
        } else if (PackageInfo.INSTALL_LOCATION_PREFER_EXTERNAL == installLocation) {
            location = context.getString(R.string.install_location_prefer_eternal);
        }
        Log.d(TAG, "getInstallLocation=>location: " + location);
        return location;
    }

    public static String getPermissionsString(Context context, PackageInfo info) {
        StringBuilder sb = new StringBuilder();
        PackageManager pm = context.getPackageManager();
        if (info.permissions != null) {
            for (PermissionInfo pi : info.permissions) {
                sb.append(pi.name);
            }
        }
        return sb.toString();
    }

    public static View getPermissionsView(Context context, PackageInfo info) {
        AppSecurityPermissions perms = new AppSecurityPermissions(context, info);
        return perms.getPermissionsView(AppSecurityPermissions.WHICH_ALL);
    }

    /**
     * 强制停止应用
     * 非系统应用，执行下面代码失败
     * @param context
     * @param item
     */
    public static void forceStopApplication(Context context, PackageItem item) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Class clazz = am.getClass();
        try {
            Method forceStopPackageAsUser = clazz.getDeclaredMethod("forceStopPackageAsUser", String.class, int.class);
            int userId = getUserId(context, item);
            if (userId != Integer.MIN_VALUE) {
                forceStopPackageAsUser.invoke(am, item.getPackageName(), userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "forceStopApplication=>error: ", e);
        }
    }

    public static int getUserId(Context context, PackageItem item) {
        int userId = Integer.MIN_VALUE;
        Class clazz = UserHandle.class;
        try {
            Method getUserId = clazz.getDeclaredMethod("getUserId", int.class);
            userId = (int) getUserId.invoke(null, item.getUid());
        } catch (Exception e) {
            Log.e(TAG, "getUserId=>error: ", e);
        }
        return userId;
    }

}
