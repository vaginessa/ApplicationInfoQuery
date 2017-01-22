package com.android.aiq.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.android.aiq.Log;
import com.android.aiq.R;
import com.android.aiq.model.InfoListItem;
import com.android.aiq.model.ListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16-12-19.
 */

public class BroadcastUtils {

    private static final String TAG = "BroadcastUtils";

    public static ArrayList<ListItem> getBroadcastsList(Context context, String packageName) {
        ArrayList<ListItem> list = new ArrayList<ListItem>();
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
                    list.add(new ListItem(icon, name, pn, className));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getActivitiesList=>error: ", e);
        }
        return list;
    }

    public static ArrayList<ListItem> getBroadcastsListByIntent(Context context, Intent intent) {
        ArrayList<ListItem> list = new ArrayList<ListItem>();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> ais = pm.queryBroadcastReceivers(intent, PackageManager.MATCH_ALL);
        if (ais != null) {
            Drawable icon = null;
            String name = null;
            String pn = null;
            String className = null;
            for (ResolveInfo info : ais) {
                icon = info.loadIcon(pm);
                if (icon == null) {
                    icon = context.getDrawable(R.mipmap.ic_launcher);
                }
                name = info.activityInfo.loadLabel(pm).toString();
                pn = info.activityInfo.packageName;
                className = info.activityInfo.name;
                list.add(new ListItem(icon, name, pn, className));
            }
        }
        return list;
    }

    public static ArrayList<InfoListItem> getBroadcastInfos(Context context, String packageName, String className) {
        ArrayList<InfoListItem> list = new ArrayList<InfoListItem>();
        ActivityInfo ai = getBroadcastInfo(context, packageName, className);
        PackageManager pm = context.getPackageManager();
        if (ai != null) {
            list.add(new InfoListItem(R.string.info_name, ai.loadLabel(pm).toString()));
            list.add(new InfoListItem(R.string.package_name, ai.packageName));
            list.add(new InfoListItem(R.string.class_name, ai.name));
            list.add(new InfoListItem(R.string.target_activity, ai.targetActivity));
            list.add(new InfoListItem(R.string.parent_activity_name, ai.parentActivityName));
            list.add(new InfoListItem(R.string.task_affinity, ai.taskAffinity));
            list.add(new InfoListItem(R.string.process_name, ai.processName));
            list.add(new InfoListItem(R.string.permissions, ai.permission));
            list.add(new InfoListItem(R.string.config_changes, ai.configChanges + ""));
            list.add(new InfoListItem(R.string.document_launch_mode, ai.documentLaunchMode + ""));
            list.add(new InfoListItem(R.string.flags, ai.flags + ""));
            list.add(new InfoListItem(R.string.theme_resource, ai.getThemeResource() + ""));
            list.add(new InfoListItem(R.string.launch_mode, ai.launchMode + ""));
            list.add(new InfoListItem(R.string.max_recents, ai.maxRecents + ""));
            list.add(new InfoListItem(R.string.persistable_mode, ai.persistableMode + ""));
            list.add(new InfoListItem(R.string.screen_orientation, ai.screenOrientation + ""));
            list.add(new InfoListItem(R.string.soft_input_mode, ai.softInputMode + ""));
            list.add(new InfoListItem(R.string.theme, ai.theme + ""));
            list.add(new InfoListItem(R.string.ui_options, ai.uiOptions + ""));
            list.add(new InfoListItem(R.string.flags, ai.flags + ""));
            //list.add(new InfoListItem(R.string.window_layout, ai.windowLayout + ""));
            list.add(new InfoListItem(R.string.banner, ai.banner + ""));
            list.add(new InfoListItem(R.string.description_res, ai.descriptionRes + ""));
            //list.add(new InfoListItem(R.string.direct_boot_awre, ai.directBootAware + ""));
            list.add(new InfoListItem(R.string.enabled, ai.enabled + ""));
            list.add(new InfoListItem(R.string.exported, ai.exported + ""));
            list.add(new InfoListItem(R.string.banner_resource, ai.getBannerResource() + ""));
            list.add(new InfoListItem(R.string.icon_resource, ai.getIconResource() + ""));
            list.add(new InfoListItem(R.string.logo_resource, ai.getLogoResource() + ""));
            list.add(new InfoListItem(R.string.icon, ai.icon + ""));
            list.add(new InfoListItem(R.string.label_res, ai.labelRes + ""));
            list.add(new InfoListItem(R.string.logo, ai.logo + ""));
            list.add(new InfoListItem(R.string.meta_data, ai.metaData != null ? ai.metaData.toString() : ""));
            list.add(new InfoListItem(R.string.non_localized_label, ai.nonLocalizedLabel + ""));
        }
        return list;
    }

    public static ActivityInfo getBroadcastInfo(Context context, String packageName, String className) {
        ActivityInfo ai = null;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
            ActivityInfo[] ais = pi.receivers;
            if (ais != null) {
                for (ActivityInfo info : ais) {
                    if (info.name.equals(className)) {
                        ai = info;
                        break;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getBroadcastInfo=>error: ", e);
            ai = null;
        }
        return ai;
    }
}
