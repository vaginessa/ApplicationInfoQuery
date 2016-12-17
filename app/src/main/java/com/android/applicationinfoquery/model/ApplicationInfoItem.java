package com.android.applicationinfoquery.model;

/**
 * Created by user on 16-12-16.
 */

public class ApplicationInfoItem {

    private int mTitle;
    private String mInfo;

    public ApplicationInfoItem(int title, String info) {
        mTitle = title;
        mInfo = info;
    }

    public int getTitle() {
        return mTitle;
    }

    public String getInfo() {
        return mInfo;
    }

    @Override
    public String toString() {
        return "[" + mTitle + ", " + mInfo + "]";
    }
}
