package com.android.aiq;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String TAG = "Utils";

    public static final String EXTRA_TYPE = "type";
    public static final int TYPE_ALL_APPLICATION = 0;
    public static final int TYPE_SYSTEM_APPLICATION = 1;
    public static final int TYPE_NON_SYSTEM_APPLICATION = 2;

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
    private static Intent getLauncherIntent(Context context, String packageName) {
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
        String path = null;
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
        String path = null;
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

}
