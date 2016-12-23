package com.android.aiq.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
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

public class ServiceUtils {

    private static final String TAG = "ServiceUtils";

    public static ArrayList<ListItem> getServicesList(Context context, String packageName) {
        ArrayList<ListItem> list = new ArrayList<ListItem>();
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
                    list.add(new ListItem(icon, name, pn, className));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getActivitiesList=>error: ", e);
        }
        return list;
    }

    public static ArrayList<ListItem> getServicesListByIntent(Context context, Intent intent) {
        ArrayList<ListItem> list = new ArrayList<ListItem>();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> ris = pm.queryIntentServices(intent, PackageManager.MATCH_ALL);
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
                name = info.serviceInfo.loadLabel(pm).toString();
                pn = info.serviceInfo.packageName;
                className = info.serviceInfo.name;
                list.add(new ListItem(icon, name, pn, className));
            }
        }
        return list;
    }

    public static ArrayList<InfoListItem> getServiceInfos(Context context, String packageName, String className) {
        ArrayList<InfoListItem> list = new ArrayList<InfoListItem>();
        PackageManager pm = context.getPackageManager();
        ServiceInfo si = getServiceInfo(context, packageName, className);
        if (si != null) {
            list.add(new InfoListItem(R.string.name, si.loadLabel(pm).toString()));
            list.add(new InfoListItem(R.string.package_name, si.packageName));
            list.add(new InfoListItem(R.string.process_name, si.processName));
            list.add(new InfoListItem(R.string.logo_resource, si.getLogoResource() + ""));
            list.add(new InfoListItem(R.string.exported, si.exported + ""));
            list.add(new InfoListItem(R.string.icon_resource, si.getIconResource() + ""));
            list.add(new InfoListItem(R.string.enabled, si.enabled + ""));
            list.add(new InfoListItem(R.string.banner_resource, si.getBannerResource() + ""));
            list.add(new InfoListItem(R.string.non_localized_label, (si.nonLocalizedLabel != null ? si.nonLocalizedLabel.toString() : "")));
            //list.add(new InfoListItem(R.string.direct_boot_awre, si.directBootAware + ""));
            list.add(new InfoListItem(R.string.permissions, si.permission));
            list.add(new InfoListItem(R.string.flags, si.flags + ""));
            list.add(new InfoListItem(R.string.banner, si.banner + ""));
            list.add(new InfoListItem(R.string.label_res, si.labelRes + ""));
            list.add(new InfoListItem(R.string.decription_res, si.descriptionRes + ""));
            list.add(new InfoListItem(R.string.icon, si.icon + ""));
            list.add(new InfoListItem(R.string.logo, si.logo + ""));
        }
        return list;
    }

    public static ServiceInfo getServiceInfo(Context context, String packageName, String className) {
        ServiceInfo si = null;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SERVICES);
            ServiceInfo[] sis = pi.services;
            if (sis != null) {
                for (ServiceInfo info : sis) {
                    if (info.name.equals(className)) {
                        si = info;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getServiceInfo=>error: ", e);
            si = null;
        }
        return si;
    }
}
