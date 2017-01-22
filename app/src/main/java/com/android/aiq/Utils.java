package com.android.aiq;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureGroupInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String TAG = "Utils";

    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_APK_PATH = "path";
    public static final int TYPE_ALL_APPLICATION = 0;
    public static final int TYPE_SYSTEM_APPLICATION = 1;
    public static final int TYPE_NON_SYSTEM_APPLICATION = 2;

    public static final int USER_MODE = 0;
    public static final int DEVELOPER_MODE = 1;

    public static final int ACTION_NONE = -1;
    public static final int ACTION_DIALOG = 0;
    public static final int ACTION_ACTIVITY = 1;

    public static final int TYPE_NONE = -1;
    public static final int TYPE_PI_APPLICATION_INFO = 0;
    public static final int TYPE_PI_ACTIVITIES = 1;
    public static final int TYPE_PI_SERVICES = 2;
    public static final int TYPE_PI_RECEIVERS = 3;
    public static final int TYPE_PI_PROVIDERS = 4;
    public static final int TYPE_PI_INSTRUMENTATION = 5;
    public static final int TYPE_PI_GIDS = 6;
    public static final int TYPE_PI_SPLIT_REVISION_CODES = 7;
    public static final int TYPE_PI_SPLIT_NAMES = 8;
    public static final int TYPE_PI_SIGNATURES = 9;
    public static final int TYPE_PI_FEATURE_GROUPS = 10;
    public static final int TYPE_PI_PERMISSIONS = 11;
    public static final int TYPE_PI_CONFIG_PREFERENCES = 12;
    public static final int TYPE_PI_REQUESTED_PERMISSIONS = 13;
    public static final int TYPE_REQUESTED_PERMISSIONS_FLAGS = 14;

    /**
     * 创建圆形进度对话框
     *
     * @param context 　Context对象
     * @param resId   　对话框要显示的文本资源Id
     * @return 返回圆形进度对话框对象
     */
    public static Dialog createLoadProgressDialog(Context context, int resId) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.ProgressDialogTheme);
        dialog.setMessage(context.getString(resId));
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 通过应用类型，获取设备已安装应用信息列表
     *
     * @param context 　Context对象
     * @param type    　应用类型，可以是{@link #TYPE_ALL_APPLICATION}、{@link #TYPE_SYSTEM_APPLICATION}、{@link #TYPE_NON_SYSTEM_APPLICATION}
     * @return 如果存在相对应的应用，则返回所有应用信息列表；否则返回空列表
     */
    public static ArrayList<ListItem> getApplicationList(Context context, int type) {
        ArrayList<ListItem> list = new ArrayList<ListItem>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infos = pm.getInstalledPackages(0);
        if (infos != null) {
            PackageInfo pi = null;
            Intent intent = null;
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
                intent = getLauncherIntent(context, packageName);
                if (intent != null) {
                    launcherActivity = intent.getComponent().getClassName();
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

    /**
     * 获取应用的启动活动Intent对象
     *
     * @param context     　Context对象
     * @param packageName 　应用的包名
     * @return 如果应用有启动活动, 则返回对应活动的Intent对象；否则返回null
     */
    public static Intent getLauncherIntent(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        return intent;
    }

    /**
     * 判断应用是否是系统应用
     *
     * @param info 应用的PackageInfo对象
     * @return 如果是系统应用，返回true；否则返回false
     */
    public static boolean isSystemApp(PackageInfo info) {
        return ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    /**
     * 判断应用是否是系统更新应用
     *
     * @param info 　应用的PackageInfo对象
     * @return 如果是系统更新应用，返回true；否则返回false
     */
    public static boolean isSystemUpdateApp(PackageInfo info) {
        return ((info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    /**
     * 启动Activity
     *
     * @param context      Context对象
     * @param packageName  　应用的包名
     * @param activityName Activity的完整类名
     */
    public static void startActivity(Context context, String packageName, String activityName) {
        Intent intent = new Intent();
        ComponentName name = new ComponentName(packageName, activityName);
        intent.setComponent(name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断应用是否启用
     *
     * @param context     　Context对象
     * @param packageName 　要判断应用的包名
     * @return 如果应用的设置为{@see android.content.pm.PackageManager$OMPONENT_ENABLED_STATE_DEFAULT}和{@see android.content.pm.PackageManager$COMPONENT_ENABLED_STATE_ENABLED}表示应用为启用状态；
     * 如果应用的设置为{@see android.content.pm.PackageManager$COMPONENT_ENABLED_STATE_DISABLED}、{@see android.content.pm.PackageManager$COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED}和
     * {@see android.content.pm.PackageManager$COMPONENT_ENABLED_STATE_DISABLED_USER}表示应用为禁用状态
     */
    public static boolean isApplicationEnabled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        int setting = pm.getApplicationEnabledSetting(packageName);
        switch (setting) {
            case PackageManager.COMPONENT_ENABLED_STATE_DEFAULT:
            case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                return true;

            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED:
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER:
                return false;

            default:
                return true;
        }
    }

    /**
     * 设置应用启用或禁用，此功能需要系统权限
     *
     * @param context     　Context对象
     * @param packageName 　应用的包名
     * @param enabled     　true表示启用应用，false表示禁用应用
     */
    public static void setApplicationEnabled(Context context, String packageName, boolean enabled) {
        PackageManager pm = context.getPackageManager();
        if (enabled) {
            pm.setApplicationEnabledSetting(packageName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            pm.setApplicationEnabledSetting(packageName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    /**
     * 卸载应用
     *
     * @param context     　Context对象
     * @param packageName 　要卸载的应用的包名
     */
    public static void uninstallApplication(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent delete = new Intent();
        delete.setAction(Intent.ACTION_DELETE);
        delete.setData(uri);
        context.startActivity(delete);
    }

    /**
     * 拷贝应用apk到外部存储中
     *
     * @param context     Context对象
     * @param packageName 　应用的包名
     */
    public static boolean copyApplication(Context context, String packageName) {
        boolean result = false;
        PackageManager pm = context.getPackageManager();
        String apkPath = getApplicationFilePath(context, packageName);
        String copyPath = getApplicationFileCopyPath(context, packageName);
        if (!TextUtils.isEmpty(apkPath) && !TextUtils.isEmpty(copyPath)) {
            result = copyFile(apkPath, copyPath);
        }
        return result;
    }

    /**
     * 获取应用apk文件路径
     *
     * @param context     　Context对象
     * @param packageName 应用的包名
     * @return 获取成功，返回apk文件路径，否则返回null
     */
    public static String getApplicationFilePath(Context context, String packageName) {
        String path;
        PackageManager pm = context.getPackageManager();
        ApplicationInfo info = null;
        try {
            info = pm.getApplicationInfo(packageName, 0);
            path = info.publicSourceDir;
            if (TextUtils.isEmpty(path)) {
                path = info.sourceDir;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getApplicationFilePath=>error: ", e);
            path = null;
        }
        return path;
    }

    /**
     * 获取应用apk文件的拷贝路径
     *
     * @param context     　Context对象
     * @param packageName 　应用的包名
     * @return 返回拷贝路径（/Android/data/com.android.aiq/应用名称/应用名称.apk）
     */
    public static String getApplicationFileCopyPath(Context context, String packageName) {
        String path;
        PackageManager pm = context.getPackageManager();
        ApplicationInfo info = null;
        try {
            info = pm.getApplicationInfo(packageName, 0);
            String name = info.loadLabel(pm).toString();
            File file = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            file = new File(file, name + "/" + name + ".apk");
            path = file.getAbsolutePath();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getApplicationFileCopyPath=>error: ", e);
            path = null;
        }
        return path;
    }

    /**
     * 拷贝文件夹
     *
     * @param src 源文件夹路径
     * @param des 　目标文件夹路径
     * @return 拷贝成功返回true, 否则返回false
     */
    private static boolean copyDirectory(String src, String des) {
        boolean result = true;
        File srcFile = new File(src);
        File[] fs = srcFile.listFiles();
        if (fs != null) {
            File desFile = new File(des);
            if (!desFile.exists()) {
                desFile.mkdirs();
            }
            for (File f : fs) {
                if (f.isFile()) {
                    boolean success = copyFile(f.getPath(), des + "\\" + f.getName());
                    if (!success) {
                        result = false;
                    }
                } else if (f.isDirectory()) {
                    copyDirectory(f.getPath(), des + "\\" + f.getName());
                }
            }
        }
        return result;
    }

    /**
     * 拷贝文件
     *
     * @param src 　源文件路径
     * @param des 　目标文件路径
     * @return 拷贝成功返回true, 否则返回false
     */
    public static boolean copyFile(String src, String des) {
        boolean result = false;
        FileInputStream in = null;
        FileOutputStream out = null;
        File toFile = new File(des);
        File fromFile = new File(src);
        if (fromFile.exists() && fromFile.isFile()) {
            if (toFile.exists()) {
                toFile.delete();
            } else {
                toFile.getParentFile().mkdirs();
            }
            try {
                in = new FileInputStream(src);
                out = new FileOutputStream(des);
                int len = -1;
                byte[] buffer = new byte[2048];
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                in.close();
                result = true;
            } catch (Exception e) {
                Log.e(TAG, "copyFile=>error: ", e);
                result = false;
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                } catch (IOException e) {
                }

            }
        }
        return result;
    }

    /**
     * 获取应用名称
     *
     * @param context     　Context对象
     * @param packageName 应用的包名
     * @return 获取成功返回应用名称，否则返回未知应用字符串
     */
    public static String getApplicationLabel(Context context, String packageName) {
        String label = "";
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            label = info.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            label = context.getString(R.string.unknown_applicatin_name);
            Log.e(TAG, "getApplicationLabel=>error: ", e);
        }
        return label;
    }

    /**
     * 获取应用名称
     *
     * @param context 　Context对象
     * @param info    应用的PackageInfo对象
     * @return 获取成功返回应用名称，否则返回未知应用字符串
     */
    public static String getApplicationLabel(Context context, PackageInfo info) {
        PackageManager pm = context.getPackageManager();
        String label = info.applicationInfo.loadLabel(pm).toString();
        return label;
    }

    /**
     * 获取已安装应用的PackageInfo对象
     *
     * @param context     　Context对象
     * @param packageName 　应用的包名
     * @return 获取成功返回PackageInfo对象，否则返回null
     */
    public static PackageInfo getPackageInfoByPackageName(Context context, String packageName, int flags) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getPackageInfo=>error: ", e);
            info = null;
        }
        return info;
    }

    /**
     * 获取未安装应用的PackageInfo对象
     *
     * @param context 　Context对象
     * @param path    　应用apk文件的路径
     * @param flags   Additional option flags. Use any combination of
     *                {@see PackageManager$GET_ACTIVITIES}, {@see PackageManager$GET_CONFIGURATIONS},
     *                {@see PackageManager$GET_GIDS}, {@see PackageManager$GET_INSTRUMENTATION},
     *                {@see PackageManager$GET_INTENT_FILTERS}, {@see PackageManager$GET_META_DATA},
     *                {@see PackageManager$GET_PERMISSIONS}, {@see PackageManager$GET_PROVIDERS},
     *                {@see PackageManager$GET_RECEIVERS}, {@see PackageManager$GET_SERVICES},
     *                {@see PackageManager$GET_SHARED_LIBRARY_FILES}, {@see PackageManager$GET_SIGNATURES},
     *                {@see PackageManager$GET_URI_PERMISSION_PATTERNS}, {@see PackageManager$GET_UNINSTALLED_PACKAGES},
     *                {@see PackageManager$MATCH_DISABLED_COMPONENTS}, {@see PackageManager$MATCH_DISABLED_UNTIL_USED_COMPONENTS},
     *                {@see PackageManager$MATCH_UNINSTALLED_PACKAGES}
     *                to modify the data returned.
     * @return 获取成功返回PackageInfo对象，否则返回null
     */
    public static PackageInfo getPackageInfoByPath(Context context, String path, int flags) {
        PackageInfo info = context.getPackageManager().getPackageArchiveInfo(path, 0);
        return info;
    }

    /**
     * 安装apk
     *
     * @param context Context对象
     * @param path    　apk文件路径
     */
    public static void installApk(Context context, String path) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        context.startActivity(install);
    }

    /**
     * 获取ListView的EmptyView
     *
     * @param context Context对象
     * @param resId   　要显示信息的资源id
     * @return 返回TextView对象
     */
    public static TextView getEmptyView(Context context, int resId) {
        TextView emptyView = new TextView(context);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, context.getResources().getInteger(R.integer.empty_view_text_size));
        emptyView.setText(resId);
        emptyView.setVisibility(View.GONE);
        return emptyView;
    }

    /**
     * 获取应用PackageInfo信息集合
     *
     * @param context 　Context对象
     * @param info    应用的PackageInfo对象
     * @param mode    　信息显示模式，{@link #USER_MODE}和{@link #DEVELOPER_MODE}
     * @return PackageInfo信息集合
     */
    public static ArrayList<InfoItem> getPackageInfoList(Context context, PackageInfo info, int mode) {
        ArrayList<InfoItem> list = new ArrayList<InfoItem>();
        PackageManager pm = context.getPackageManager();
        if (info != null) {
            list.add(new InfoItem(R.string.info_name, ACTION_NONE, TYPE_NONE, info.applicationInfo.loadLabel(pm).toString().trim()));
            list.add(new InfoItem(R.string.info_version_name, ACTION_NONE, TYPE_NONE, info.versionName));
            list.add(new InfoItem(R.string.info_version_code, ACTION_NONE, TYPE_NONE, info.versionCode + ""));
            list.add(new InfoItem(R.string.info_core_app, ACTION_NONE, TYPE_NONE, getCoreApp(context, info, mode)));
            list.add(new InfoItem(R.string.info_package_name, ACTION_NONE, TYPE_NONE, info.packageName));
            list.add(new InfoItem(R.string.info_shared_user_id, ACTION_NONE, TYPE_NONE, info.sharedUserId + ""));
            list.add(new InfoItem(R.string.info_first_install_time, ACTION_NONE, TYPE_NONE, formatDate(context, info.firstInstallTime)));
            list.add(new InfoItem(R.string.info_last_update_time, ACTION_NONE, TYPE_NONE, formatDate(context, info.lastUpdateTime)));
            list.add(new InfoItem(R.string.info_install_location, ACTION_NONE, TYPE_NONE, getInstallLocationDescription(info.installLocation, mode)));
            list.add(new InfoItem(R.string.info_applicationInfo, ACTION_ACTIVITY, TYPE_PI_APPLICATION_INFO, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_activities, ACTION_ACTIVITY, TYPE_PI_ACTIVITIES, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_services, ACTION_ACTIVITY, TYPE_PI_SERVICES, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_receivers, ACTION_ACTIVITY, TYPE_PI_RECEIVERS, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_providers, ACTION_ACTIVITY, TYPE_PI_PROVIDERS, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_instrumentation, ACTION_ACTIVITY, TYPE_PI_INSTRUMENTATION, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_gids, ACTION_DIALOG, TYPE_PI_GIDS, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_base_revision_code, ACTION_NONE, TYPE_NONE, info.baseRevisionCode + ""));
            list.add(new InfoItem(R.string.info_shared_user_label, ACTION_NONE, TYPE_NONE, getSharedUserLabelDescription(context, info, mode)));
            list.add(new InfoItem(R.string.info_required_for_all_users, ACTION_NONE, TYPE_NONE, getRequiredForAllUsers(context, info, mode)));
            list.add(new InfoItem(R.string.info_restricted_account_type, ACTION_NONE, TYPE_NONE, getRestrictedAccountType(context, info, mode)));
            list.add(new InfoItem(R.string.info_required_account_type, ACTION_NONE, TYPE_NONE, getRequiredAccountType(context, info, mode)));
            list.add(new InfoItem(R.string.info_overlay_target, ACTION_NONE, TYPE_NONE, getOverlayTarget(context, info, mode)));
            list.add(new InfoItem(R.string.info_split_revision_codes, ACTION_DIALOG, TYPE_PI_SPLIT_REVISION_CODES, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_split_names, ACTION_DIALOG, TYPE_PI_SPLIT_NAMES, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_signatures, ACTION_ACTIVITY, TYPE_PI_SIGNATURES, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_feature_groups, ACTION_ACTIVITY, TYPE_PI_FEATURE_GROUPS, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_permissions, ACTION_ACTIVITY, TYPE_PI_PERMISSIONS, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_config_preferences, ACTION_ACTIVITY, TYPE_PI_CONFIG_PREFERENCES, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_requested_permissions, ACTION_DIALOG, TYPE_PI_REQUESTED_PERMISSIONS, context.getString(R.string.click_to_view)));
            list.add(new InfoItem(R.string.info_requested_permissions_flags, ACTION_DIALOG, TYPE_REQUESTED_PERMISSIONS_FLAGS, context.getString(R.string.click_to_view)));
        }
        return list;
    }

    public static String getCoreApp(Context context, PackageInfo info, int mode) {
        boolean coreApp = false;
        Class clazz = PackageInfo.class;
        try {
            Field field = clazz.getField("coreApp");
            coreApp = field.getBoolean(info);
        } catch (Exception e) {
            Log.e(TAG, "getCoreApp=>error: ", e);
        }
        if (mode == USER_MODE) {
            return coreApp ? context.getString(R.string.yes) : context.getString(R.string.no);
        } else {
            return Boolean.toString(coreApp);
        }
    }

    public static String getRequiredForAllUsers(Context context, PackageInfo info, int mode) {
        boolean requiredForAllUsers = false;
        Class clazz = PackageInfo.class;
        try {
            Field field = clazz.getField("requiredForAllUsers");
            requiredForAllUsers = field.getBoolean(info);
        } catch (Exception e) {
            Log.e(TAG, "getRequiredForAllUsers=>error: ", e);
        }
        if (mode == USER_MODE) {
            return requiredForAllUsers ? context.getString(R.string.yes) : context.getString(R.string.no);
        } else {
            return Boolean.toString(requiredForAllUsers);
        }
    }

    public static String getRestrictedAccountType(Context context, PackageInfo info, int mode) {
        String restrictedAccountType = "";
        Class clazz = PackageInfo.class;
        try {
            Field field = clazz.getField("restrictedAccountType");
            restrictedAccountType = (String) field.get(info);
        } catch (Exception e) {
            Log.e(TAG, "getRestrictedAccountType=>error: ", e);
        }
        return restrictedAccountType;
    }

    public static String getRequiredAccountType(Context context, PackageInfo info, int mode) {
        String requiredAccountType = "";
        Class clazz = PackageInfo.class;
        try {
            Field field = clazz.getField("requiredAccountType");
            requiredAccountType = (String) field.get(info);
        } catch (Exception e) {
            Log.e(TAG, "getRequiredAccountType=>error: ", e);
        }
        return requiredAccountType;
    }

    public static String getOverlayTarget(Context context, PackageInfo info, int mode) {
        String overlayTarget = "";
        Class clazz = PackageInfo.class;
        try {
            Field field = clazz.getField("overlayTarget");
            overlayTarget = (String) field.get(info);
        } catch (Exception e) {
            Log.e(TAG, "getOverlayTarget=>error: ", e);
        }
        return overlayTarget;
    }

    public static String getSharedUserLabelDescription(Context context, PackageInfo info, int mode) {
        if (mode == USER_MODE) {
            if (info.sharedUserLabel > 0) {
                String labeDescription = "";
                try {
                    Context packageContext = context.createPackageContext(info.packageName, Context.CONTEXT_IGNORE_SECURITY);
                    labeDescription = packageContext.getString(info.sharedUserLabel);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "getSharedUserLabelDescription=>error: ", e);
                    labeDescription = info.sharedUserId + "";
                }
                return labeDescription;
            } else {
                return info.sharedUserLabel + "";
            }
        } else {
            return info.sharedUserLabel + "";
        }
    }

    /**
     * 获取安装位置描述符
     *
     * @param installLocation 　安装位置标志
     * @param mode            　显示模式
     * @return 安装位置描述字符串
     */
    public static String getInstallLocationDescription(int installLocation, int mode) {
        if (mode == Utils.DEVELOPER_MODE) {
            return installLocation + "";
        } else {
            switch (installLocation) {
                case PackageInfo.INSTALL_LOCATION_AUTO:
                    return "INSTALL_LOCATION_AUTO";

                case PackageInfo.INSTALL_LOCATION_INTERNAL_ONLY:
                    return "INSTALL_LOCATION_INTERNAL_ONLY";

                case PackageInfo.INSTALL_LOCATION_PREFER_EXTERNAL:
                    return "INSTALL_LOCATION_PREFER_EXTERNAL";

                default:
                    return "INSTALL_LOCATION_UNSPECIFIED";
            }
        }
    }

    public static Drawable getApplicationIcon(Context context, PackageInfo info) {
        Drawable icon = info.applicationInfo.loadIcon(context.getPackageManager());
        if (icon == null) {
            icon = context.getDrawable(R.mipmap.ic_launcher);
        }
        return icon;
    }

    public static Dialog createInfoDialog(Context context, int type, String packageName, String apkPath, int mode) {
        AlertDialog dialog = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        if (!TextUtils.isEmpty(packageName)) {
            info = getPackageInfoByPackageName(context, packageName, getFlags(type));
        } else {
            info = getPackageInfoByPath(context, apkPath, getFlags(type));
        }
        if (info != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setIcon(getApplicationIcon(context, info))
                    .setTitle(getApplicationLabel(context, info.packageName))
                    .setPositiveButton(android.R.string.ok, null);
            switch (type) {
                case Utils.TYPE_PI_GIDS:
                    builder.setMessage(getGidsDescription(context, info.gids, mode));
                    break;

                case Utils.TYPE_PI_SPLIT_REVISION_CODES:
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        builder.setMessage(getSplitRevisionCodesDescription(context, info.splitRevisionCodes, mode));
                    }
                    break;

                case Utils.TYPE_PI_SPLIT_NAMES:
                    builder.setMessage(getSplitNameDescription(context, info.splitNames, mode));
                    break;

                case Utils.TYPE_PI_REQUESTED_PERMISSIONS:
                    builder.setMessage(getRequestedPermissionsDescription(context, info.requestedPermissions, mode));
                    break;

                case Utils.TYPE_REQUESTED_PERMISSIONS_FLAGS:
                    builder.setMessage(getRequestedPermissionsFlagsDescription(context, info.requestedPermissionsFlags, mode));
                    break;
            }
            dialog = builder.create();
        }
        return dialog;
    }

    private static String getRequestedPermissionsFlagsDescription(Context context, int[] requestedPermissionsFlags, int mode) {
        String requestedPermissionsFlagsDes = "";
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.head_requested_permissions_flags));
        if (requestedPermissionsFlags != null) {
            for (int i = 0; i < requestedPermissionsFlags.length; i++) {
                sb.append("\n");
                sb.append("\t\t" + requestedPermissionsFlags[i]);
            }
            requestedPermissionsFlagsDes = sb.toString();
        } else {
            if (mode == USER_MODE) {
                sb.append("\n");
                sb.append("\t\t");
                requestedPermissionsFlagsDes = sb.toString();
            } else {
                sb.append("\n");
                sb.append("\t\tnull");
                requestedPermissionsFlagsDes = sb.toString();
            }
        }
        return requestedPermissionsFlagsDes;
    }

    private static String getRequestedPermissionsDescription(Context context, String[] requestedPermissions, int mode) {
        String requestedPermissionsDes = "";
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.head_requested_permissions));
        if (requestedPermissions != null) {
            for (int i = 0; i < requestedPermissions.length; i++) {
                sb.append("\n");
                if (mode == USER_MODE) {
                    sb.append("\t\t" + requestedPermissions[i].substring(requestedPermissions[i].lastIndexOf(".") + 1, requestedPermissions[i].length()));
                } else {
                    sb.append("\t\t" + requestedPermissions[i]);
                }
            }
            requestedPermissionsDes = sb.toString();
        } else {
            if (mode == USER_MODE) {
                sb.append("\n");
                sb.append("\t\t");
                requestedPermissionsDes = sb.toString();
            } else {
                sb.append("\n");
                sb.append("\t\tnull");
                requestedPermissionsDes = sb.toString();
            }
        }
        return requestedPermissionsDes;
    }

    private static String getSplitNameDescription(Context context, String[] splitNames, int mode) {
        String splitNamesDes = "";
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.head_split_names));
        if (splitNames != null) {
            for (int i = 0; i < splitNames.length; i++) {
                sb.append("\n");
                sb.append("\t\t" + splitNames[i]);
            }
            splitNamesDes = sb.toString();
        } else {
            if (mode == USER_MODE) {
                sb.append("\n");
                sb.append("\t\t");
                splitNamesDes = sb.toString();
            } else {
                sb.append("\n");
                sb.append("\t\tnull");
                splitNamesDes = sb.toString();
            }
        }
        return splitNamesDes;
    }

    public static String getSplitRevisionCodesDescription(Context context, int[] splitRevisionCodes, int mode) {
        String splitRevisionCodesDes = "";
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.head_split_revision_codes));
        if (splitRevisionCodes != null) {
            for (int i = 0; i < splitRevisionCodes.length; i++) {
                sb.append("\n");
                sb.append("\t\t" + splitRevisionCodes[i]);
            }
            splitRevisionCodesDes = sb.toString();
        } else {
            if (mode == USER_MODE) {
                sb.append("\n");
                sb.append("\t\t");
                splitRevisionCodesDes = sb.toString();
            } else {
                sb.append("\n");
                sb.append("\t\tnull");
                splitRevisionCodesDes = sb.toString();
            }
        }
        return splitRevisionCodesDes;
    }

    public static int getFlags(int type) {
        int flags = 0;
        switch (type) {
            case TYPE_PI_ACTIVITIES:
                flags |= PackageManager.GET_ACTIVITIES;
                break;

            case TYPE_PI_SERVICES:
                flags |= PackageManager.GET_SERVICES;
                break;

            case TYPE_PI_RECEIVERS:
                flags |= PackageManager.GET_RECEIVERS;
                break;

            case TYPE_PI_PROVIDERS:
                flags |= PackageManager.GET_PROVIDERS;
                break;

            case TYPE_PI_CONFIG_PREFERENCES:
                flags |= PackageManager.GET_CONFIGURATIONS;
                break;

            case TYPE_PI_GIDS:
                flags |= PackageManager.GET_GIDS;
                break;

            case TYPE_PI_INSTRUMENTATION:
                flags |= PackageManager.GET_INSTRUMENTATION;
                break;

            case TYPE_PI_REQUESTED_PERMISSIONS:
            case TYPE_PI_PERMISSIONS:
                flags |= PackageManager.GET_PERMISSIONS;
                break;

            case TYPE_PI_SIGNATURES:
                flags |= PackageManager.GET_SIGNATURES;
                break;
        }
        return flags;
    }

    public static String getGidsDescription(Context context, int[] gids, int mode) {
        String gidDescription = "";
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.head_gids));
        if (gids != null) {
            for (int i = 0; i < gids.length; i++) {
                sb.append("\n");
                sb.append("\t\t" + gids[i]);
            }
            gidDescription = sb.toString();
        } else {
            if (mode == USER_MODE) {
                sb.append("\n");
                sb.append("\t\t");
                gidDescription = sb.toString();
            } else {
                sb.append("\n");
                sb.append("\t\tnull");
                gidDescription = sb.toString();
            }
        }
        return gidDescription;
    }

    /**
     * 获取ConfigurationInfo描述
     * @param context　Context对象
     * @param configs　ConfigurationInfo数组
     * @param mode　显示模式
     * @return 返回ConfigurationInfo描述字符串
     */
    public static String getConfigPreferencesDescription(Context context, ConfigurationInfo[] configs, int mode) {
        String configPreferences = "";
        if (configs != null) {
            StringBuilder sb = new StringBuilder();
            ConfigurationInfo info = null;
            for (int i = 0; i < configs.length; i++) {
                info = configs[i];
                if (i != 0) {
                    sb.append("\n");
                }
                sb.append(context.getString(R.string.head_config_preferences, i) + "\n");
                sb.append(context.getString(R.string.head_gl_es_version, info.getGlEsVersion()) + "\n");
                sb.append(context.getString(R.string.head_req_gl_es_version, info.reqGlEsVersion) + "\n");
                sb.append(context.getString(R.string.head_req_input_features, info.reqInputFeatures) + "\n");
                sb.append(context.getString(R.string.head_req_keyboard_type, info.reqKeyboardType) + "\n");
                sb.append(context.getString(R.string.head_req_navigation, info.reqNavigation) + "\n");
                sb.append(context.getString(R.string.head_req_touch_screen, info.reqTouchScreen));
            }
            configPreferences = sb.toString();
        } else {
            if (mode == USER_MODE) {
                configPreferences = "";
            } else {
                configPreferences = "null";
            }
        }
        return configPreferences;
    }

    /**
     * 获取应用的功能组信息描述
     * @param context　Context对象
     * @param featureGroups　功能组数组
     * @param mode　显示模式
     * @return 返回功能组描述信息字符串
     */
    private static String getFeatureGroupsDescription(Context context, FeatureGroupInfo[] featureGroups, int mode) {
        String featureGroupsDes = "";
        if (featureGroups != null) {
            StringBuilder sb = new StringBuilder();
            FeatureGroupInfo info = null;
            for (int i = 0; i < featureGroups.length; i++) {
                info = featureGroups[i];
                if (i != 0) {
                    sb.append("\n");
                }
                sb.append(context.getString(R.string.head_feature_group, i) + "\n");
                FeatureInfo[] fis = info.features;
                if (fis != null) {
                    FeatureInfo fi = null;
                    for (int j = 0; j < fis.length; j++) {
                        fi = fis[i];
                        if (j != 0) {
                            sb.append("\n");
                        }
                        sb.append(context.getString(R.string.head_feature_info, i) + "\n");
                        sb.append("\t" + context.getString(R.string.head_name, fi.name) + "\n");
                        sb.append("\t" + context.getString(R.string.head_version, fi.version) + "\n");
                        sb.append("\t" + context.getString(R.string.head_flags, fi.flags) + "\n");
                        sb.append("\t" + context.getString(R.string.head_gl_es_version, fi.getGlEsVersion()) + "\n");
                        sb.append("\t" + context.getString(R.string.head_req_gl_es_version, fi.reqGlEsVersion) + "\n");
                    }
                } else {
                    sb.append(" \n");
                }
            }
            featureGroupsDes = sb.toString();
        } else {
            if (mode == USER_MODE) {
                featureGroupsDes = "";
            } else {
                featureGroupsDes = "null";
            }
        }
        return featureGroupsDes;
    }

    public static String getPermissionsDescription(Context context, PermissionInfo[] permissions, int mode) {
        if (permissions != null) {
            StringBuilder sb = new StringBuilder();
            PackageManager pm = context.getPackageManager();
            PermissionInfo info = null;
            for (int i = 0; i < permissions.length; i++) {
                info = permissions[i];
                if (i != 0) {
                    sb.append("\n");
                }
                sb.append(context.getString(R.string.head_permission, i) + "\n");
                sb.append("\t" + context.getString(R.string.head_name, info.name));
                sb.append("\t" + context.getString(R.string.head_description, info.loadDescription(pm)) + "\n");
                sb.append("\t" + context.getString(R.string.head_protection_level, getProtectionLevelDescription(info.protectionLevel, mode)) + "\n");
            }
            return sb.toString();
        } else {
            if (mode == USER_MODE) {
                return "";
            } else {
                return "null";
            }
        }
    }

    public static String getProtectionLevelDescription(int level, int mode) {
        if (mode == USER_MODE) {
            StringBuilder sb = new StringBuilder();
            switch (level) {
                case PermissionInfo.PROTECTION_NORMAL:
                    return "PROTECTION_NORMAL";

                case PermissionInfo.PROTECTION_DANGEROUS:
                    return "PROTECTION_DANGEROUS";

                case PermissionInfo.PROTECTION_SIGNATURE:
                    return "PROTECTION_SIGNATURE";

                default:
                    if ((level & PermissionInfo.PROTECTION_FLAG_PRIVILEGED) != 0) {
                        sb.append("PROTECTION_MASK_FLAGS");
                    }
                    if ((level & PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM) != 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("PROTECTION_SIGNATURE_OR_SYSTEM");
                    }
                    if ((level & PermissionInfo.PROTECTION_FLAG_PRIVILEGED) != 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("PROTECTION_FLAG_PRIVILEGED");
                    }
                    if ((level & PermissionInfo.PROTECTION_FLAG_DEVELOPMENT) != 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("PROTECTION_FLAG_DEVELOPMENT");
                    }
                    if ((level & PermissionInfo.PROTECTION_FLAG_APPOP) != 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("PROTECTION_FLAG_APPOP");
                    }
                    if ((level & PermissionInfo.PROTECTION_FLAG_PRE23) != 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("PROTECTION_FLAG_PRE23");
                    }
                    if ((level & PermissionInfo.PROTECTION_FLAG_INSTALLER) != 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("PROTECTION_FLAG_INSTALLER");
                    }
                    if ((level & PermissionInfo.PROTECTION_FLAG_VERIFIER) != 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("PROTECTION_FLAG_VERIFIER");
                    }
                    if ((level & PermissionInfo.PROTECTION_FLAG_VERIFIER) != 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("PROTECTION_FLAG_PREINSTALLED");
                    }
                    if ((level & PermissionInfo.PROTECTION_FLAG_PREINSTALLED) != 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("PROTECTION_FLAG_PREINSTALLED");
                    }
                    if ((level & PermissionInfo.PROTECTION_FLAG_SETUP) != 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("PROTECTION_FLAG_SETUP");
                    }
            }
            return sb.toString();
        } else {
            return level + "";
        }
    }

    /**
     * 格式化时间
     *
     * @param time 　时间值
     * @return 格式化为1998-12-23 15:56:18
     */
    public static String formatDate(Context context, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (time > 0) {
            return sdf.format(time);
        } else {
            return context.getString(R.string.uninstall);
        }
    }

    public static void startActivityByAction(Context context, int type, String mPackageName, String mApkPath, int mMode) {
        switch (type) {
            case Utils.TYPE_PI_APPLICATION_INFO:
                break;

            case Utils.TYPE_PI_ACTIVITIES:

                break;

            case Utils.TYPE_PI_SERVICES:

                break;

            case Utils.TYPE_PI_RECEIVERS:

                break;

            case Utils.TYPE_PI_PROVIDERS:

                break;

            case Utils.TYPE_PI_INSTRUMENTATION:

                break;

            case Utils.TYPE_PI_SIGNATURES:

                break;

            case Utils.TYPE_PI_FEATURE_GROUPS:

                break;

            case Utils.TYPE_PI_PERMISSIONS:

                break;

            case Utils.TYPE_PI_CONFIG_PREFERENCES:
                break;
        }
    }
}
