package com.android.aiq.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.aiq.Log;
import com.android.aiq.R;
import com.android.aiq.model.InfoListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by user on 16-12-19.
 */

public class ApplicationUtils {

    private static final String TAG = "ApplicationUtils";

    public static ArrayList<InfoListItem> getApplicationInfos(Context context, String packageName) {
        ArrayList<InfoListItem> list = new ArrayList<InfoListItem>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(packageName, 0);
            if (info != null) {
                list.add(new InfoListItem(R.string.application_name, info.applicationInfo.loadLabel(pm).toString()));
                list.add(new InfoListItem(R.string.version, info.versionName));
                list.add(new InfoListItem(R.string.base_revision_code, info.baseRevisionCode + ""));
                list.add(new InfoListItem(R.string.shared_user_id, info.sharedUserId));
                list.add(new InfoListItem(R.string.package_name, info.packageName));
                list.add(new InfoListItem(R.string.launcer_activity, Utils.getLauncherActivity(context, info.packageName)));
                list.add(new InfoListItem(R.string.first_install_time, sdf.format(info.firstInstallTime)));
                list.add(new InfoListItem(R.string.last_update_time, sdf.format(info.lastUpdateTime)));
                list.add(new InfoListItem(R.string.install_location, Utils.getInstallLocation(context, info.installLocation)));
                list.add(new InfoListItem(R.string.shared_user_label, info.sharedUserLabel + ""));
                list.add(new InfoListItem(R.string.back_up_agent_name, info.applicationInfo.backupAgentName));
                list.add(new InfoListItem(R.string.application_class_name, info.applicationInfo.className));
                list.add(new InfoListItem(R.string.data_dir, info.applicationInfo.dataDir));
                list.add(new InfoListItem(R.string.native_library_dir, info.applicationInfo.nativeLibraryDir));
                list.add(new InfoListItem(R.string.manage_space_activity_name, info.applicationInfo.manageSpaceActivityName));
                list.add(new InfoListItem(R.string.process_name, info.applicationInfo.processName));
                list.add(new InfoListItem(R.string.public_source_dir, info.applicationInfo.publicSourceDir));
                list.add(new InfoListItem(R.string.source_dir, info.applicationInfo.sourceDir));
                list.add(new InfoListItem(R.string.task_affinity, info.applicationInfo.taskAffinity));
                list.add(new InfoListItem(R.string.description, info.applicationInfo.loadDescription(pm) + ""));
                list.add(new InfoListItem(R.string.enabled, info.applicationInfo.enabled + ""));
                list.add(new InfoListItem(R.string.flags, info.applicationInfo.flags + ""));
                list.add(new InfoListItem(R.string.largest_width_limit_dp, info.applicationInfo.largestWidthLimitDp + ""));
                list.add(new InfoListItem(R.string.require_smallest_width_dp, info.applicationInfo.requiresSmallestWidthDp + ""));
                list.add(new InfoListItem(R.string.uid, info.applicationInfo.uid + ""));
                list.add(new InfoListItem(R.string.ui_options, info.applicationInfo.uiOptions + ""));
                list.add(new InfoListItem(R.string.theme, info.applicationInfo.theme + ""));
                list.add(new InfoListItem(R.string.compatible_width_limit_dp, info.applicationInfo.compatibleWidthLimitDp + ""));
                list.add(new InfoListItem(R.string.decription_res, info.applicationInfo.descriptionRes + ""));
                list.add(new InfoListItem(R.string.feature, ""));
                list.add(new InfoListItem(R.string.permissions, ""));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getApplicationInfos=>error: ", e);
        }
        return list;
    }
}
