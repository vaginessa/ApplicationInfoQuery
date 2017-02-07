package com.android.aiq;

import android.app.Activity;
import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.content.pm.InstrumentationInfo;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Created by user on 17-2-6.
 */

public class InstrumentationActivity extends ListActivity {

    InstrumentationInfo i;

    /*
    i.nonLocalizedLabel;
        i.packageName;
        i.logo;
        i.labelRes;
        i.icon;
        i.dataDir;
        i.functionalTest;
        i.handleProfiling;
        i.publicSourceDir;
        i.sourceDir;
        i.splitPublicSourceDirs;
        i.splitSourceDirs;
        i.targetPackage;
        i.name;
        i.metaData.keySet();
     */

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
}
