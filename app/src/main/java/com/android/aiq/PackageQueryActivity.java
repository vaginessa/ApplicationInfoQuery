package com.android.aiq;

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

public class PackageQueryActivity extends Activity implements View.OnClickListener {

    private EditText mPackageNameEt;
    private Button mQueryBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_query);

        initViews();
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
        String packageName = mPackageNameEt.getText().toString().trim();
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this, "onClick=>error: ", e);
            info = null;
        }
        Log.d(this, "onClick=>packageName: " + packageName + " info: " + info);
        if (info != null) {
            Intent intent = new Intent(this, ApplicationInfoList.class);
            intent.putExtra(Utils.EXTRA_PACKAGE_NAME, packageName);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.not_find_package, packageName), Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mPackageNameEt = (EditText) findViewById(R.id.package_name);
        mQueryBt = (Button) findViewById(R.id.query);

        mPackageNameEt.addTextChangedListener(mPackageNameWatcher);
        mQueryBt.setOnClickListener(this);
        mQueryBt.setEnabled(false);
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
