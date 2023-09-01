package com.kingocean.warehouseapp.utils;

import android.os.StrictMode;
public class ThreadUtils {
    public static void setThreadPolicyPermitAll() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
