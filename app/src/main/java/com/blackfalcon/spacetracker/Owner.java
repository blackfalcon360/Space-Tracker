package com.blackfalcon.spacetracker;

import java.util.Locale;

/**
 * Best guess of which app or activity produced a file, from its path only.
 * Android does not record who wrote a file, so this is a guess. Pure Java.
 * Returns "pkg:<package.name>", "key:<name>" or "".
 */
final class Owner {
    private Owner() {}

    static String guess(String path) {
        String p = path.toLowerCase(Locale.ROOT);
        String[] roots = {"/android/media/", "/android/data/", "/android/obb/"};
        for (String r : roots) {
            int i = p.indexOf(r);
            if (i >= 0) {
                int s = i + r.length();
                int e = p.indexOf('/', s);
                String pkg = e < 0 ? p.substring(s) : p.substring(s, e);
                if (pkg.indexOf('.') > 0) return "pkg:" + pkg;
            }
        }
        String[][] keys = {
                {"/whatsapp", "whatsapp"}, {"/telegram", "telegram"}, {"/instagram", "instagram"},
                {"/snapchat", "snapchat"}, {"/tiktok", "tiktok"}, {"/fb_img", "facebook"}, {"/facebook", "facebook"},
                {"/shareit", "shareit"}, {"/xender", "xender"}, {"/bluetooth", "bluetooth"},
                {"/dcim/camera", "camera"}, {"/dcim/screenshots", "screenshots"}, {"/pictures/screenshots", "screenshots"},
                {"/screenrecorder", "recordings"}, {"/screen recordings", "recordings"}, {"/recordings", "recordings"},
                {"/recorder", "recordings"}, {"/call", "recordings"},
                {"/download", "downloads"}, {"/music", "music"}, {"/movies", "movies"},
                {"/.thumbnails", "thumbs"}, {"/dcim/.thumbnails", "thumbs"}, {"/documents", "documents"},
                {"/cache", "cache"}, {"/.cache", "cache"}, {"/temp", "cache"}, {"/tmp", "cache"},
        };
        for (String[] k : keys) {
            if (p.contains(k[0] + "/") || p.endsWith(k[0])) return "key:" + k[1];
        }
        return "";
    }
}
