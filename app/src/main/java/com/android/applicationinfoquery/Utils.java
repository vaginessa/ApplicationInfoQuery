package com.android.applicationinfoquery;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AppSecurityPermissions;

import com.android.applicationinfoquery.model.ActivityItem;
import com.android.applicationinfoquery.model.ApplicationInfoItem;
import com.android.applicationinfoquery.model.BroadcastItem;
import com.android.applicationinfoquery.model.PackageItem;
import com.android.applicationinfoquery.model.ProviderItem;
import com.android.applicationinfoquery.model.ServiceItem;
import com.android.applicationinfoquery.model.Type;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/12/16.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static final String EXTRA_PACKAGE_TYPE = "package_type";
    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_APPLICATION_LABEL = "application_label";

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
                launcherActivity = getLauncherActivity(context, packageName);
                if (launcherActivity.equals(context.getString(R.string.not_find_launcher_activity))) {
                    hasLauncherActivity = false;
                } else {
                    hasLauncherActivity = true;
                }
                isSystemApp = isSystemApp(info) || isSystemUpdateApp(info);
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

    public static ArrayList<ApplicationInfoItem> getApplicationInfos(Context context, String packageName) {
        ArrayList<ApplicationInfoItem> list = new ArrayList<ApplicationInfoItem>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(packageName, 0);
            if (info != null) {
                list.add(new ApplicationInfoItem(R.string.application_name, info.applicationInfo.loadLabel(pm).toString()));
                list.add(new ApplicationInfoItem(R.string.version, info.versionName));
                list.add(new ApplicationInfoItem(R.string.base_revision_code, info.baseRevisionCode + ""));
                list.add(new ApplicationInfoItem(R.string.shared_user_id, info.sharedUserId));
                list.add(new ApplicationInfoItem(R.string.package_name, info.packageName));
                list.add(new ApplicationInfoItem(R.string.launcer_activity, getLauncherActivity(context, info.packageName)));
                list.add(new ApplicationInfoItem(R.string.first_install_time, sdf.format(info.firstInstallTime)));
                list.add(new ApplicationInfoItem(R.string.last_update_time, sdf.format(info.lastUpdateTime)));
                list.add(new ApplicationInfoItem(R.string.install_location, getInstallLocation(context, info.installLocation)));
                list.add(new ApplicationInfoItem(R.string.shared_user_label, info.sharedUserLabel + ""));
                list.add(new ApplicationInfoItem(R.string.back_up_agent_name, info.applicationInfo.backupAgentName));
                list.add(new ApplicationInfoItem(R.string.application_class_name, info.applicationInfo.className));
                list.add(new ApplicationInfoItem(R.string.data_dir, info.applicationInfo.dataDir));
                list.add(new ApplicationInfoItem(R.string.native_library_dir, info.applicationInfo.nativeLibraryDir));
                list.add(new ApplicationInfoItem(R.string.manage_space_activity_name, info.applicationInfo.manageSpaceActivityName));
                list.add(new ApplicationInfoItem(R.string.process_name, info.applicationInfo.processName));
                list.add(new ApplicationInfoItem(R.string.public_source_dir, info.applicationInfo.publicSourceDir));
                list.add(new ApplicationInfoItem(R.string.source_dir, info.applicationInfo.sourceDir));
                list.add(new ApplicationInfoItem(R.string.task_affinity, info.applicationInfo.taskAffinity));
                list.add(new ApplicationInfoItem(R.string.description, info.applicationInfo.loadDescription(pm) + ""));
                list.add(new ApplicationInfoItem(R.string.enabled, info.applicationInfo.enabled + ""));
                list.add(new ApplicationInfoItem(R.string.flags, info.applicationInfo.flags + ""));
                list.add(new ApplicationInfoItem(R.string.largest_width_limit_dp, info.applicationInfo.largestWidthLimitDp + ""));
                list.add(new ApplicationInfoItem(R.string.require_smallest_width_dp, info.applicationInfo.requiresSmallestWidthDp + ""));
                list.add(new ApplicationInfoItem(R.string.uid, info.applicationInfo.uid + ""));
                list.add(new ApplicationInfoItem(R.string.ui_options, info.applicationInfo.uiOptions + ""));
                list.add(new ApplicationInfoItem(R.string.theme, info.applicationInfo.theme + ""));
                list.add(new ApplicationInfoItem(R.string.compatible_width_limit_dp, info.applicationInfo.compatibleWidthLimitDp + ""));
                list.add(new ApplicationInfoItem(R.string.decription_res, info.applicationInfo.descriptionRes + ""));
                list.add(new ApplicationInfoItem(R.string.feature, ""));
                list.add(new ApplicationInfoItem(R.string.permissions, ""));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getApplicationInfos=>error: ", e);
        }
        return list;
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

    public static ArrayList<ActivityItem> getActivitiesList(Context context, String packageName) {
        ArrayList<ActivityItem> list = new ArrayList<ActivityItem>();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] infos = info.activities;
            if (infos != null) {
                Drawable icon = null;
                String name = null;
                String pn = null;
                String className = null;
                for (ActivityInfo ai : infos) {
                    icon = ai.loadIcon(pm);
                    if (icon == null) {
                        icon = context.getDrawable(R.mipmap.ic_launcher);
                    }
                    name = ai.loadLabel(pm).toString();
                    pn = ai.packageName;
                    className = ai.name;
                    list.add(new ActivityItem(icon, name, pn, className));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getActivitiesList=>error: ", e);
        }
        return list;
    }

    public static ArrayList<ServiceItem> getServicesList(Context context, String packageName) {
        ArrayList<ServiceItem> list = new ArrayList<ServiceItem>();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_SERVICES);
            ServiceInfo[] infos = info.services;
            if (infos != null) {
                Drawable icon = null;
                String name = null;
                String pn = null;
                String className = null;
                for (ServiceInfo ai : infos) {
                    icon = ai.loadIcon(pm);
                    if (icon == null) {
                        icon = context.getDrawable(R.mipmap.ic_launcher);
                    }
                    name = ai.loadLabel(pm).toString();
                    pn = ai.packageName;
                    className = ai.name;
                    list.add(new ServiceItem(icon, name, pn, className));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getActivitiesList=>error: ", e);
        }
        return list;
    }

    public static ArrayList<BroadcastItem> getBroadcastsList(Context context, String packageName) {
        ArrayList<BroadcastItem> list = new ArrayList<BroadcastItem>();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
            ActivityInfo[] infos = info.receivers;
            if (infos != null) {
                Drawable icon = null;
                String name = null;
                String pn = null;
                String className = null;
                for (ActivityInfo ai : infos) {
                    icon = ai.loadIcon(pm);
                    if (icon == null) {
                        icon = context.getDrawable(R.mipmap.ic_launcher);
                    }
                    name = ai.loadLabel(pm).toString();
                    pn = ai.packageName;
                    className = ai.name;
                    list.add(new BroadcastItem(icon, name, pn, className));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getActivitiesList=>error: ", e);
        }
        return list;
    }

    public static ArrayList<ProviderItem> getProvidersList(Context context, String packageName) {
        ArrayList<ProviderItem> list = new ArrayList<ProviderItem>();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_PROVIDERS);
            ProviderInfo[] infos = info.providers;
            if (infos != null) {
                Drawable icon = null;
                String name = null;
                String pn = null;
                String className = null;
                for (ProviderInfo ai : infos) {
                    icon = ai.loadIcon(pm);
                    if (icon == null) {
                        icon = context.getDrawable(R.mipmap.ic_launcher);
                    }
                    name = ai.loadLabel(pm).toString();
                    pn = ai.packageName;
                    className = ai.name;
                    list.add(new ProviderItem(icon, name, pn, className));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getActivitiesList=>error: ", e);
        }
        return list;
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
