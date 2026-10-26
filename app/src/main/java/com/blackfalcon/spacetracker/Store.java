package com.blackfalcon.spacetracker;

import android.content.Context;
import android.content.SharedPreferences;

final class Store {
    private Store() {}

    private static SharedPreferences p(Context c) {
        return c.getApplicationContext().getSharedPreferences("spacetracker", Context.MODE_PRIVATE);
    }

    static String lang(Context c) { return p(c).getString("lang", "en"); }
    static void setLang(Context c, String v) { p(c).edit().putString("lang", v).apply(); }
    static int langIdx(Context c) {
        String l = lang(c);
        return "ur".equals(l) ? 1 : ("ar".equals(l) ? 2 : 0);
    }

    static boolean dark(Context c) { return p(c).getBoolean("dark", true); }
    static void setDark(Context c, boolean v) { p(c).edit().putBoolean("dark", v).apply(); }

    // per-app size snapshots: "time\npkg|total\npkg|total..."
    static String snap(Context c, String which) { return p(c).getString("snap_" + which, ""); }
    static void setSnap(Context c, String which, String v) { p(c).edit().putString("snap_" + which, v).apply(); }
}
