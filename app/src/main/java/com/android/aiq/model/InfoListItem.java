package com.android.aiq.model;

/**
 * Created by user on 16-12-19.
 */

public class InfoListItem {

    private int mTitle;
    private String mInfo;

    public InfoListItem(int title, String info) {
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
