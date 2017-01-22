package com.android.aiq.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.android.aiq.Log;
import com.android.aiq.R;
import com.android.aiq.model.InfoListItem;
import com.android.aiq.model.ListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 16-12-19.
 */

public class ProviderUtils {

    private static final String TAG = "ProviderUtils";

    public static ArrayList<ListItem> getProvidersList(Context context, String packageName) {
        ArrayList<ListItem> list = new ArrayList<ListItem>();
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
                    list.add(new ListItem(icon, name, pn, className));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getActivitiesList=>error: ", e);
        }
        return list;
    }

    public static ArrayList<ListItem> getProvidersListByIntent(Context context, Intent intent) {
        ArrayList<ListItem> list = new ArrayList<ListItem>();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> ris = pm.queryIntentContentProviders(intent, PackageManager.MATCH_ALL);
        if (ris != null) {
            Drawable icon = null;
            String name = null;
            String pn = null;
            String className = null;
            for (ResolveInfo info : ris) {
                icon = info.loadIcon(pm);
                if (icon == null) {
                    icon = context.getDrawable(R.mipmap.ic_launcher);
                }
                name = info.providerInfo.loadLabel(pm).toString();
                pn = info.providerInfo.packageName;
                className = info.providerInfo.name;
                list.add(new ListItem(icon, name, pn, className));
            }
        }
        return list;
    }

    public static ArrayList<InfoListItem> getProviderInfos(Context context, String packageName, String className) {
        ArrayList<InfoListItem> list = new ArrayList<InfoListItem>();
        ProviderInfo pi = getProviderInfo(context, packageName, className);
        PackageManager pm = context.getPackageManager();
        if (pi != null) {
            list.add(new InfoListItem(R.string.info_name, pi.loadLabel(pm).toString()));
            list.add(new InfoListItem(R.string.package_name, pi.packageName));
            list.add(new InfoListItem(R.string.class_name, pi.name));
            list.add(new InfoListItem(R.string.decription_res, pi.descriptionRes + ""));
            list.add(new InfoListItem(R.string.banner_resource, pi.getBannerResource() + ""));
            list.add(new InfoListItem(R.string.logo, pi.logo + ""));
            list.add(new InfoListItem(R.string.icon_resource, pi.getIconResource() + ""));
            list.add(new InfoListItem(R.string.enabled, pi.enabled + ""));
            list.add(new InfoListItem(R.string.label_res, pi.labelRes + ""));
            list.add(new InfoListItem(R.string.icon, pi.icon + ""));
            list.add(new InfoListItem(R.string.exported, pi.exported + ""));
            list.add(new InfoListItem(R.string.authority, pi.authority));
            list.add(new InfoListItem(R.string.read_permission, pi.readPermission));
            list.add(new InfoListItem(R.string.write_permission, pi.writePermission));
            list.add(new InfoListItem(R.string.process_name, pi.processName));
            list.add(new InfoListItem(R.string.flags, pi.flags + ""));
            list.add(new InfoListItem(R.string.grant_uri_permissions, pi.grantUriPermissions + ""));
            list.add(new InfoListItem(R.string.init_order, pi.initOrder + ""));
            list.add(new InfoListItem(R.string.multi_process, pi.multiprocess + ""));
            list.add(new InfoListItem(R.string.path_permissions, pi.pathPermissions != null ? Arrays.toString(pi.pathPermissions) : ""));
            list.add(new InfoListItem(R.string.uri_permission_patterns, pi.uriPermissionPatterns != null ? Arrays.toString(pi.uriPermissionPatterns) : ""));
            list.add(new InfoListItem(R.string.banner, pi.banner + ""));
            list.add(new InfoListItem(R.string.meta_data, pi.metaData != null ? pi.metaData.toString() : ""));
            list.add(new InfoListItem(R.string.non_localized_label, pi.nonLocalizedLabel != null ? pi.nonLocalizedLabel.toString() : ""));
            list.add(new InfoListItem(R.string.is_syncable, pi.isSyncable + ""));
        }
        return list;
    }

    public static ProviderInfo getProviderInfo(Context context, String packageName, String className) {
        ProviderInfo pi = null;
        PackageManager pm = context.getPackageManager();
        Log.d(TAG, "getProviderInfo=>class name: " + className);
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PROVIDERS);
            ProviderInfo[] pis = packageInfo.providers;
            if (pis != null) {
                for (ProviderInfo info : pis) {
                    Log.d(TAG, "getProviderInfo=>name: " + info.name);
                    if (info.name.equals(className)) {
                        pi = info;
                        break;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getProviderInfo=>error: ", e);
            pi = null;
        }
        return pi;
    }
}
