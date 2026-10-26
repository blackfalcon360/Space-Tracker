package com.blackfalcon.spacetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Walks a folder tree once and keeps: totals per folder (rolled up), bytes added in the last hour / day /
 * week / month per folder, the newest files and the biggest files overall and per time window.
 * Pure Java so it can be tested on a computer.
 */
final class Scanner {
    static final long H = 3600_000L;
    static final long[] WIN = {H, 24 * H, 7 * 24 * H, 30 * 24 * H};
    static final int ALL = 4; // index of the "all time" list

    static final class Rec {
        String path;
        long size;
        long time;
    }

    static final class Dir {
        String name, path;
        Dir parent;
        final ArrayList<Dir> kids = new ArrayList<Dir>();
        long size, files;
        final long[] add = new long[4];
    }

    final long now;
    final int cap;
    final int maxDepth;
    volatile boolean cancel = false;
    volatile long scannedFiles = 0;
    volatile long scannedBytes = 0;
    volatile String current = "";
    Dir root;

    private final PriorityQueue<Rec> newest;
    private final List<PriorityQueue<Rec>> big = new ArrayList<PriorityQueue<Rec>>();

    Scanner(long now, int cap) {
        this.now = now;
        this.cap = cap;
        this.maxDepth = 40;
        newest = new PriorityQueue<Rec>(cap + 1, new Comparator<Rec>() {
            @Override
            public int compare(Rec a, Rec b) { return Long.compare(a.time, b.time); }
        });
        for (int i = 0; i < 5; i++) {
            big.add(new PriorityQueue<Rec>(cap + 1, new Comparator<Rec>() {
                @Override
                public int compare(Rec a, Rec b) { return Long.compare(a.size, b.size); }
            }));
        }
    }

    Dir scan(File rootDir) {
        root = walk(rootDir, null, 0);
        return root;
    }

    private Dir walk(File dir, Dir parent, int depth) {
        Dir d = new Dir();
        d.name = parent == null ? dir.getPath() : dir.getName();
        d.path = dir.getPath();
        d.parent = parent;
        if (cancel || depth > maxDepth) return d;
        current = d.path;
        File[] list = dir.listFiles();
        if (list == null) return d;
        for (File f : list) {
            if (cancel) break;
            if (f.isDirectory()) {
                if (isLink(f)) continue;
                Dir k = walk(f, d, depth + 1);
                d.kids.add(k);
                d.size += k.size;
                d.files += k.files;
                for (int i = 0; i < 4; i++) d.add[i] += k.add[i];
            } else {
                long len = f.length();
                long mt = f.lastModified();
                if (mt > now) mt = now;
                d.size += len;
                d.files++;
                scannedFiles++;
                scannedBytes += len;
                if (mt > 0) {
                    long age = now - mt;
                    for (int i = 0; i < 4; i++) {
                        if (age <= WIN[i]) {
                            d.add[i] += len;
                            offerBig(i, f, len, mt);
                        }
                    }
                    offerNewest(f, len, mt);
                }
                offerBig(ALL, f, len, mt);
            }
        }
        return d;
    }

    private static boolean isLink(File f) {
        try {
            return !f.getCanonicalPath().equals(f.getAbsolutePath());
        } catch (Exception e) {
            return true;
        }
    }

    private void offerNewest(File f, long len, long mt) {
        if (newest.size() >= cap && mt <= newest.peek().time) return;
        Rec r = rec(f, len, mt);
        if (newest.size() >= cap) newest.poll();
        newest.add(r);
    }

    private void offerBig(int idx, File f, long len, long mt) {
        PriorityQueue<Rec> q = big.get(idx);
        if (q.size() >= cap && len <= q.peek().size) return;
        Rec r = rec(f, len, mt);
        if (q.size() >= cap) q.poll();
        q.add(r);
    }

    private static Rec rec(File f, long len, long mt) {
        Rec r = new Rec();
        r.path = f.getPath();
        r.size = len;
        r.time = mt;
        return r;
    }

    /** Newest files first. */
    List<Rec> newestList() {
        List<Rec> l = new ArrayList<Rec>(newest);
        Collections.sort(l, new Comparator<Rec>() {
            @Override
            public int compare(Rec a, Rec b) { return Long.compare(b.time, a.time); }
        });
        return l;
    }

    /** Biggest first. window 0..3 = last hour/day/week/month, 4 = all time. */
    List<Rec> bigList(int window) {
        List<Rec> l = new ArrayList<Rec>(big.get(window));
        Collections.sort(l, new Comparator<Rec>() {
            @Override
            public int compare(Rec a, Rec b) { return Long.compare(b.size, a.size); }
        });
        return l;
    }
}
