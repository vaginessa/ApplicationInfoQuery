package com.android.aiq;

public class InfoItem {

    int titleId;
    int action;
    int type;
    String info;

    public InfoItem(int titleId, int action, int type, String info) {
        this.titleId = titleId;
        this.action = action;
        this.type = type;
        this.info = info;
    }
}
