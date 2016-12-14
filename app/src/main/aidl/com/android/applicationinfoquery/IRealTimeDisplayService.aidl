// IRealTimeDisplayService.aidl
package com.android.applicationinfoquery;

// Declare any non-default types here with import statements

interface IRealTimeDisplayService {

    boolean isShowing();
    void showRealTimeInfo();
    void cancelRealTimeInfo();

}
