package com.android.aiq;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class UninstallApplication extends Activity implements View.OnClickListener {

    private static final int SELECT_APK_FILE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uninstall_application);

        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_APK_FILE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String path = uri.getPath();
                Log.d(this, "onActivityResult=>uri: " + uri + " path: " + path);
                if (!TextUtils.isEmpty(path) && path.endsWith(".apk")) {
                    Intent applicationInfo = new Intent(this, ApplicationInfoList.class);
                    applicationInfo.putExtra(Utils.EXTRA_APK_PATH, path);
                    startActivity(applicationInfo);
                } else {
                    Toast.makeText(this, R.string.invalid_file, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.no_files_selected, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onClick(View v) {
        //启动apk文件选择器
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.android.package-archive");
        startActivityForResult(intent, SELECT_APK_FILE);
    }

    private void initViews() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Button selectBt = (Button) findViewById(R.id.select_apk_file);

        selectBt.setOnClickListener(this);
    }

}