package com.blackfalcon.spacetracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Formatting helpers. Pure Java. Sizes use 1000-based units, like Android's own storage screen. */
final class Fmt {
    private Fmt() {}

    static String size(long b) {
        if (b < 0) b = 0;
        if (b < 1000) return b + " B";
        String[] u = {"KB", "MB", "GB", "TB"};
        double v = b / 1000.0;
        int i = 0;
        while (v >= 1000 && i < u.length - 1) {
            v /= 1000.0;
            i++;
        }
        if (v >= 100) return String.format(Locale.US, "%.0f %s", v, u[i]);
        if (v >= 10) return String.format(Locale.US, "%.1f %s", v, u[i]);
        return String.format(Locale.US, "%.2f %s", v, u[i]);
    }

    /** Age in English short form: "5 min", "3 h", "2 d". */
    static String age(long ms) {
        if (ms < 0) ms = 0;
        long s = ms / 1000;
        if (s < 60) return s + " s";
        long m = s / 60;
        if (m < 60) return m + " min";
        long h = m / 60;
        if (h < 48) return h + " h";
        long d = h / 24;
        if (d < 60) return d + " d";
        long mo = d / 30;
        if (mo < 24) return mo + " mo";
        return (d / 365) + " y";
    }

    static String date(long t) {
        if (t <= 0) return "-";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date(t));
    }

    static String name(String path) {
        int i = path.lastIndexOf('/');
        return i < 0 ? path : path.substring(i + 1);
    }

    static String parent(String path) {
        int i = path.lastIndexOf('/');
        return i <= 0 ? "/" : path.substring(0, i);
    }

    /** Emoji by file extension. */
    static String icon(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        int i = n.lastIndexOf('.');
        String e = i < 0 ? "" : n.substring(i + 1);
        switch (e) {
            case "mp4": case "mkv": case "avi": case "mov": case "3gp": case "webm": case "m4v": case "ts":
                return "\uD83C\uDFAC";
            case "jpg": case "jpeg": case "png": case "gif": case "webp": case "heic": case "bmp": case "dng":
                return "\uD83D\uDDBC\uFE0F";
            case "mp3": case "m4a": case "aac": case "wav": case "flac": case "ogg": case "opus": case "amr":
                return "\uD83C\uDFB5";
            case "pdf": case "doc": case "docx": case "xls": case "xlsx": case "ppt": case "pptx": case "txt": case "epub":
                return "\uD83D\uDCC4";
            case "apk": case "xapk": case "apks":
                return "\uD83E\uDD16";
            case "zip": case "rar": case "7z": case "tar": case "gz": case "obb":
                return "\uD83D\uDCE6";
            default:
                return "\uD83D\uDCC1";
        }
    }
}
