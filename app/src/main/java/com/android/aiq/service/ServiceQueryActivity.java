package com.android.aiq.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.aiq.R;
import com.android.aiq.utils.Utils;

import static com.android.aiq.R.id.class_name_et;

public class ServiceQueryActivity extends Activity implements View.OnClickListener {

    private EditText mPackageNameEt;
    private EditText mClassNameEt;
    private EditText mActionEt;
    private Button mQueryBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_query);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mPackageNameEt = (EditText) findViewById(R.id.package_name_et);
        mClassNameEt = (EditText) findViewById(R.id.class_name_et);
        mActionEt = (EditText) findViewById(R.id.action_et);
        mQueryBt = (Button) findViewById(R.id.query_bt);

        mQueryBt.setOnClickListener(this);
        mPackageNameEt.addTextChangedListener(mTextWatcher);
        mClassNameEt.addTextChangedListener(mTextWatcher);
        mActionEt.addTextChangedListener(mTextWatcher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonStatu();
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
            case R.id.query_bt:
                queryService();
                break;
        }
    }

    private void queryService() {
        String packageName = mPackageNameEt.getText().toString().trim();
        String className = mClassNameEt.getText().toString().trim();
        String action = mActionEt.getText().toString().trim();
        Intent queryIntent = new Intent();
        if (!TextUtils.isEmpty(packageName)) {
            queryIntent.setPackage(packageName);
            if (!TextUtils.isEmpty(className)) {
                queryIntent.setClassName(packageName, className);
            }
        }
        if (!TextUtils.isEmpty(action)) {
            queryIntent.setAction(action);
        }
        Intent query = new Intent(this, ServicesListActivity.class);
        query.putExtra(Utils.EXTRA_INTENT, queryIntent);
        startActivity(query);
    }

    private void updateButtonStatu() {
        String packageName = mPackageNameEt.getText().toString();
        String action = mActionEt.getText().toString();
        if (!TextUtils.isEmpty(packageName) || !TextUtils.isEmpty(action)) {
            mQueryBt.setEnabled(true);
        } else {
            mQueryBt.setEnabled(false);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateButtonStatu();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
