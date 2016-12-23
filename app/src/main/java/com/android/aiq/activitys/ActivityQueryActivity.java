package com.android.aiq.activitys;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.aiq.R;
import com.android.aiq.utils.Utils;

public class ActivityQueryActivity extends Activity implements View.OnClickListener {

    private EditText mPackageNameEt;
    private EditText mClassNameEt;
    private EditText mActionEt;
    private EditText mCategoryEt;
    private Button mQueryBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mPackageNameEt = (EditText) findViewById(R.id.package_name_et);
        mClassNameEt = (EditText) findViewById(R.id.class_name_et);
        mActionEt = (EditText) findViewById(R.id.action_et);
        mCategoryEt = (EditText) findViewById(R.id.category_et);
        mQueryBt = (Button) findViewById(R.id.query_bt);

        mQueryBt.setOnClickListener(this);
        mPackageNameEt.addTextChangedListener(mTextWatcher);
        mClassNameEt.addTextChangedListener(mTextWatcher);
        mActionEt.addTextChangedListener(mTextWatcher);
        mCategoryEt.addTextChangedListener(mTextWatcher);
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
                queryActivity();
                break;
        }
    }

    private void queryActivity() {
        String packageName = mPackageNameEt.getText().toString();
        String className = mClassNameEt.getText().toString();
        String action = mActionEt.getText().toString();
        String category = mCategoryEt.getText().toString();
        Intent queryIntent = new Intent();
        if (!TextUtils.isEmpty(packageName)) {
            queryIntent.setPackage(packageName);
            if (!TextUtils.isEmpty(className)) {
                queryIntent.setComponent(new ComponentName(packageName, className));
            }
        }
        if (!TextUtils.isEmpty(action)) {
            queryIntent.setAction(action);
        }
        if (!TextUtils.isEmpty(category)) {
            queryIntent.addCategory(category);
        }
        Intent query = new Intent(this, ActivitiesListActivity.class);
        query.putExtra(Utils.EXTRA_INTENT, queryIntent);
        startActivity(query);
    }

    private void updateButtonStatu() {
        String packageName = mPackageNameEt.getText().toString();
        String className = mClassNameEt.getText().toString();
        String action = mActionEt.getText().toString();
        String category = mCategoryEt.getText().toString();
        if (!TextUtils.isEmpty(packageName) || !TextUtils.isEmpty(action)
                || !TextUtils.isEmpty(category) || !TextUtils.isEmpty(className)) {
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
