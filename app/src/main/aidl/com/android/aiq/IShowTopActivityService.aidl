// IRealTimeDisplayService.aidl
package com.android.aiq;

// Declare any non-default types here with import statements

interface IShowTopActivityService {

    boolean isShowing();
    void showRealTimeInfo();
    void cancelRealTimeInfo();

}
