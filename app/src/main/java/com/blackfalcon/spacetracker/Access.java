package com.blackfalcon.spacetracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

/** Storage and usage-access permissions. SDK checks live in small annotated methods. */
final class Access {
    private Access() {}

    static boolean hasFiles(Context c) {
        if (Build.VERSION.SDK_INT >= 30) return hasAllFiles();
        return c.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(30)
    private static boolean hasAllFiles() {
        return Environment.isExternalStorageManager();
    }

    static void requestFiles(Activity a) {
        if (Build.VERSION.SDK_INT >= 30) {
            openAllFilesSettings(a);
        } else {
            a.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9);
        }
    }

    @TargetApi(30)
    private static void openAllFilesSettings(Activity a) {
        try {
            a.startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:" + a.getPackageName())));
        } catch (Exception e) {
            try {
                a.startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
            } catch (Exception e2) {
                // no settings screen
            }
        }
    }

    @SuppressWarnings("deprecation")
    static boolean hasUsage(Context c) {
        try {
            AppOpsManager ops = (AppOpsManager) c.getSystemService(Context.APP_OPS_SERVICE);
            int mode = ops.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), c.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            return false;
        }
    }

    static void openUsageSettings(Activity a) {
        try {
            a.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        } catch (Exception e) {
            // no settings screen
        }
    }

    static void openAppInfo(Activity a, String pkg) {
        try {
            a.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + pkg)));
        } catch (Exception e) {
            // no settings screen
        }
    }
}
