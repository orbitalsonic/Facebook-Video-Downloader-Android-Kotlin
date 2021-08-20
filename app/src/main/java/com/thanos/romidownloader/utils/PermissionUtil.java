package com.thanos.romidownloader.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;


public class PermissionUtil {

    public static boolean useRunTimePermissions(){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    public static boolean hasPermission(Context context, String permission){
        if (useRunTimePermissions()) {
            return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (useRunTimePermissions()) {
            activity.requestPermissions(permissions, requestCode);
        }
    }

    public static boolean shouldShowRational(Activity activity, String permission) {
        if (useRunTimePermissions()) {
            return activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

}
