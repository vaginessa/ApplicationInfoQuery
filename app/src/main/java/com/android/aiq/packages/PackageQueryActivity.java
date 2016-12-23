package com.android.aiq.packages;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.aiq.ApplicationInfoQuery;
import com.android.aiq.Log;
import com.android.aiq.R;
import com.android.aiq.applications.ApplicationInfoActivity;
import com.android.aiq.utils.Utils;

public class PackageQueryActivity extends Activity implements View.OnClickListener {

    private EditText mPackageNameEt;
    private Button mQueryBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_query);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mPackageNameEt = (EditText) findViewById(R.id.package_name_et);
        mQueryBt = (Button) findViewById(R.id.query);

        mPackageNameEt.addTextChangedListener(mPackageNameWatcher);
        mQueryBt.setOnClickListener(this);
        mQueryBt.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String text = mPackageNameEt.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            mQueryBt.setEnabled(true);
        } else {
            mQueryBt.setEnabled(false);
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
        switch (v.getId()) {
            case R.id.query:
                queryPackage();
                break;
        }
    }

    private void queryPackage() {
        String packageName = mPackageNameEt.getText().toString();
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(packageName, 0);
            Intent intent = new Intent(this, PackageInfoActivity.class);
            intent.putExtra(Utils.EXTRA_APPLICATION_LABEL, info.applicationInfo.loadLabel(getPackageManager()));
            intent.putExtra(Utils.EXTRA_PACKAGE_NAME, info.packageName);
            startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this, "queryPackage=>error: ", e);
            Toast.makeText(this, getString(R.string.non_find_app, packageName), Toast.LENGTH_SHORT).show();
        }
    }

    private TextWatcher mPackageNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                mQueryBt.setEnabled(true);
            } else {
                mQueryBt.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
