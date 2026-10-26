package com.blackfalcon.spacetracker;

import android.annotation.TargetApi;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.storage.StorageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/** Per-app storage use (Android 8+, needs Usage access) and growth since an earlier scan. */
final class AppSizes {
    private AppSizes() {}

    static final class Item {
        String pkg, label;
        long app, data, cache, total;
        long before = -1; // total in the earlier snapshot, -1 if the app was not there
    }

    static boolean supported() { return Build.VERSION.SDK_INT >= 26; }

    @TargetApi(26)
    private static long[] stats(Context c, String pkg) {
        try {
            StorageStatsManager m = (StorageStatsManager) c.getSystemService(Context.STORAGE_STATS_SERVICE);
            StorageStats st = m.queryStatsForPackage(StorageManager.UUID_DEFAULT, pkg, android.os.Process.myUserHandle());
            return new long[]{st.getAppBytes(), st.getDataBytes(), st.getCacheBytes()};
        } catch (Exception e) {
            return null;
        }
    }

    static List<Item> scan(Context c) {
        List<Item> out = new ArrayList<Item>();
        if (!supported()) return out;
        PackageManager pm = c.getPackageManager();
        for (ApplicationInfo ai : pm.getInstalledApplications(0)) {
            long[] s = stats(c, ai.packageName);
            if (s == null) continue;
            Item it = new Item();
            it.pkg = ai.packageName;
            CharSequence l = ai.loadLabel(pm);
            it.label = l == null ? ai.packageName : l.toString();
            it.app = s[0];
            it.data = s[1];
            it.cache = s[2];
            it.total = s[0] + s[1];
            if (it.total > 0) out.add(it);
        }
        return out;
    }

    // ---------- snapshots ----------

    static String encode(long time, List<Item> items) {
        StringBuilder sb = new StringBuilder().append(time).append('\n');
        for (Item it : items) sb.append(it.pkg).append('|').append(it.total).append('\n');
        return sb.toString();
    }

    static long snapTime(String snap) {
        if (snap == null || snap.isEmpty()) return 0;
        try {
            return Long.parseLong(snap.substring(0, snap.indexOf('\n')));
        } catch (Exception e) {
            return 0;
        }
    }

    static HashMap<String, Long> decode(String snap) {
        HashMap<String, Long> m = new HashMap<String, Long>();
        if (snap == null || snap.isEmpty()) return m;
        String[] lines = snap.split("\n");
        for (int i = 1; i < lines.length; i++) {
            int bar = lines[i].lastIndexOf('|');
            if (bar <= 0) continue;
            try {
                m.put(lines[i].substring(0, bar), Long.parseLong(lines[i].substring(bar + 1)));
            } catch (Exception e) {
                // skip bad line
            }
        }
        return m;
    }

    static void sort(List<Item> l, final boolean byGrowth) {
        Collections.sort(l, new Comparator<Item>() {
            @Override
            public int compare(Item a, Item b) {
                if (byGrowth) {
                    long ga = a.before < 0 ? a.total : a.total - a.before;
                    long gb = b.before < 0 ? b.total : b.total - b.before;
                    if (ga != gb) return Long.compare(gb, ga);
                }
                return Long.compare(b.total, a.total);
            }
        });
    }
}
