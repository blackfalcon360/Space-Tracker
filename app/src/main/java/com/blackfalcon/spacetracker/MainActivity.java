package com.blackfalcon.spacetracker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int CAP = 300;

    // scan results survive screen changes while the process lives
    private static Scanner scanner;
    private static Scanner.Dir root;
    private static boolean scanning = false;
    private static long scanMs = 0;
    private static final List<List<Scanner.Rec>> BIG = new ArrayList<List<Scanner.Rec>>();
    private static List<Scanner.Rec> newestL = new ArrayList<Scanner.Rec>();
    private static List<AppSizes.Item> apps = null;
    private static boolean appsBusy = false;
    private static String appsNote = "";

    private Ui ui;
    private FrameLayout content;
    private final Button[] tabB = new Button[5];
    private int tab = 0;
    private final Handler H = new Handler(Looper.getMainLooper());
    private TextView scanTv;

    private int fWin = 1, fSort = 0;
    private Scanner.Dir fCur;
    private int foSort = 0;
    private boolean foFiles = false;
    private int foFilesSort = 1;
    private boolean appsByGrowth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = new Ui(this);
        build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Access.hasFiles(this) && root == null && !scanning) startScan();
        renderTab();
    }

    @Override
    public void onBackPressed() {
        if (tab == 2 && foFiles) {
            foFiles = false;
            renderTab();
            return;
        }
        if (tab == 2 && fCur != null && fCur.parent != null) {
            fCur = fCur.parent;
            renderTab();
            return;
        }
        ui.dlg(this).setTitle(ui.t("exit_title")).setMessage(ui.t("exit_msg"))
                .setPositiveButton(ui.t("yes"), (d, w) -> finish())
                .setNegativeButton(ui.t("no"), null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Access.hasFiles(this) && root == null && !scanning) startScan();
        renderTab();
    }

    // ---------- helpers ----------

    private String t(String k) { return ui.t(k); }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }

    private void copy(String s) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("path", s));
        toast(t("copied"));
    }

    private String ownerText(String path) {
        String g = Owner.guess(path);
        if (g.startsWith("pkg:")) {
            String pkg = g.substring(4);
            String label = pkg;
            try {
                PackageManager pm = getPackageManager();
                CharSequence l = pm.getApplicationInfo(pkg, 0).loadLabel(pm);
                if (l != null) label = l.toString();
            } catch (Exception e) {
                // not installed any more
            }
            return String.format(Locale.US, t("o_pkg"), label);
        }
        if (g.startsWith("key:")) return t("o_" + g.substring(4));
        return "";
    }

    private String ageText(long time) {
        return String.format(Locale.US, t("age_ago"), Fmt.age(System.currentTimeMillis() - time));
    }

    private Button chip(String text, boolean on, View.OnClickListener l) {
        Button b = ui.btn(text, on ? Ui.ORANGE : Ui.GRAY, l);
        b.setTextSize(11);
        b.setSingleLine(true);
        return b;
    }

    // ---------- shell ----------

    private void build() {
        ui.refresh();
        LinearLayout rootView = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setBackgroundColor(ui.bg);
        rootView.setPadding(ui.dp(8), ui.dp(8), ui.dp(8), ui.dp(6));
        rootView.setLayoutDirection("en".equals(Store.lang(this))
                ? View.LAYOUT_DIRECTION_LTR : View.LAYOUT_DIRECTION_RTL);

        content = new FrameLayout(this);
        rootView.addView(content, new LinearLayout.LayoutParams(Ui.MATCH, 0, 1f));

        LinearLayout bar = ui.row(rootView);
        String[] keys = {"tab_overview", "tab_files", "tab_folders", "tab_apps", "tab_settings"};
        String[] icons = {"\uD83D\uDCCA ", "\uD83D\uDCC4 ", "\uD83D\uDCC1 ", "\uD83D\uDCF1 ", "\u2699\uFE0F "};
        for (int i = 0; i < 5; i++) {
            final int idx = i;
            tabB[i] = ui.btn(icons[i] + t(keys[i]), Ui.GRAY, v -> {
                tab = idx;
                renderTab();
            });
            tabB[i].setTextSize(10);
            tabB[i].setSingleLine(true);
            tabB[i].setPadding(ui.dp(2), ui.dp(6), ui.dp(2), ui.dp(6));
            bar.addView(tabB[i], ui.weighted());
        }
        TextView by = ui.tv(t("by"), 13, ui.sub);
        by.setGravity(Gravity.CENTER);
        by.setPadding(0, ui.dp(4), 0, 0);
        rootView.addView(by, new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
        setContentView(rootView);
        renderTab();
    }

    private void renderTab() {
        if (content == null) return;
        scanTv = null;
        content.removeAllViews();
        for (int i = 0; i < 5; i++) tabB[i].setBackground(ui.rounded(i == tab ? Ui.ORANGE : Ui.GRAY, 10));
        ScrollView sv = new ScrollView(this);
        View v;
        switch (tab) {
            case 0: v = buildOverview(); break;
            case 1: v = buildFiles(); break;
            case 2: v = buildFolders(); break;
            case 3: v = buildApps(); break;
            default: v = buildSettings(); break;
        }
        sv.addView(v, new ViewGroup.LayoutParams(Ui.MATCH, Ui.WRAP));
        content.addView(sv, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
    }

    // ---------- scanning ----------

    @SuppressWarnings("deprecation")
    private void startScan() {
        if (scanning) return;
        scanning = true;
        final Scanner s = new Scanner(System.currentTimeMillis(), CAP);
        scanner = s;
        final long t0 = System.currentTimeMillis();
        new Thread(() -> {
            Scanner.Dir r = s.scan(Environment.getExternalStorageDirectory());
            runOnUiThread(() -> {
                scanning = false;
                if (!s.cancel) {
                    root = r;
                    fCur = r;
                    scanMs = System.currentTimeMillis() - t0;
                    BIG.clear();
                    for (int i = 0; i < 5; i++) BIG.add(new ArrayList<Scanner.Rec>(s.bigList(i)));
                    newestL = new ArrayList<Scanner.Rec>(s.newestList());
                    foFiles = false;
                }
                renderTab();
            });
        }).start();
        H.postDelayed(poll, 500);
        renderTab();
    }

    private final Runnable poll = new Runnable() {
        @Override
        public void run() {
            if (!scanning) return;
            if (scanTv != null && scanner != null) {
                scanTv.setText(String.format(Locale.US, t("ov_scanning"), scanner.scannedFiles));
            }
            H.postDelayed(this, 500);
        }
    };

    // ---------- Overview ----------

    private View buildOverview() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);

        // phone storage bar
        LinearLayout sc = ui.card();
        sc.addView(ui.tv("\uD83D\uDCF1  " + t("ov_storage"), 16, ui.tx));
        try {
            StatFs sf = new StatFs(Environment.getDataDirectory().getPath());
            long total = sf.getTotalBytes(), free = sf.getAvailableBytes(), used = total - free;
            LinearLayout bar = ui.row(sc);
            View u = new View(this);
            u.setBackground(ui.rounded(free < total / 10 ? Ui.RED : Ui.ORANGE, 4));
            View f = new View(this);
            f.setBackground(ui.rounded(0xFF546E7A, 4));
            LinearLayout.LayoutParams lu = new LinearLayout.LayoutParams(0, ui.dp(14), Math.max(1f, (float) used));
            LinearLayout.LayoutParams lf = new LinearLayout.LayoutParams(0, ui.dp(14), Math.max(1f, (float) free));
            lu.setMargins(0, ui.dp(8), ui.dp(2), ui.dp(8));
            lf.setMargins(ui.dp(2), ui.dp(8), 0, ui.dp(8));
            bar.addView(u, lu);
            bar.addView(f, lf);
            sc.addView(ui.tv(t("ov_used") + ": " + Fmt.size(used) + "   \u2022   " + t("ov_free") + ": " + Fmt.size(free)
                    + "   \u2022   " + Fmt.size(total), 13, ui.sub));
        } catch (Exception e) {
            sc.addView(ui.tv(t("error"), 13, ui.sub));
        }
        box.addView(sc, ui.gap());

        if (!Access.hasFiles(this)) {
            LinearLayout pc = ui.card();
            pc.addView(ui.tv(t("perm_title"), 16, ui.tx));
            TextView m = ui.tv(t("perm_msg"), 13, ui.sub);
            m.setPadding(0, ui.dp(6), 0, ui.dp(6));
            pc.addView(m);
            pc.addView(ui.btn(t("perm_btn"), Ui.GREEN, v -> Access.requestFiles(this)),
                    new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
            pc.addView(ui.tv(t("perm_back"), 12, ui.sub));
            box.addView(pc, ui.gap());
            return box;
        }

        LinearLayout r = ui.row(box);
        if (scanning) {
            scanTv = ui.tv(String.format(Locale.US, t("ov_scanning"), scanner == null ? 0 : scanner.scannedFiles), 13, ui.tx);
            r.addView(scanTv, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
            r.addView(ui.btn(t("ov_stop"), Ui.RED, v -> {
                if (scanner != null) scanner.cancel = true;
            }), new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP));
        } else {
            String st = root == null ? t("ov_start")
                    : String.format(Locale.US, t("ov_scanned"), root.files, Fmt.size(root.size), scanMs / 1000);
            TextView stv = ui.tv(st, 12, ui.sub);
            r.addView(stv, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
            r.addView(ui.btn(t("ov_scan"), Ui.GREEN, v -> startScan()), new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP));
        }
        if (root == null) return box;

        LinearLayout ac = ui.card();
        ac.addView(ui.tv("\u23F1\uFE0F  " + t("ov_added"), 16, ui.tx));
        for (int i = 0; i < 4; i++) {
            LinearLayout row = ui.row(ac);
            row.setPadding(0, ui.dp(5), 0, ui.dp(2));
            row.addView(ui.tv(t("w_" + i), 14, ui.sub), new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
            TextView val = ui.tv("+" + Fmt.size(root.add[i]), 15, root.add[i] > 1_000_000_000L ? 0xFFEF5350 : ui.tx);
            row.addView(val, new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP));
        }
        LinearLayout.LayoutParams ag = ui.gap();
        ag.setMargins(0, ui.dp(8), 0, ui.dp(8));
        box.addView(ac, ag);

        // biggest new file
        List<Scanner.Rec> b24 = BIG.size() > 1 ? BIG.get(1) : new ArrayList<Scanner.Rec>();
        List<Scanner.Rec> b7 = BIG.size() > 2 ? BIG.get(2) : new ArrayList<Scanner.Rec>();
        box.addView(header(t("ov_big_new")));
        if (b24.isEmpty()) box.addView(ui.tv(t("ov_none_new"), 13, ui.sub));
        else box.addView(fileRow(b24.get(0)), ui.gap());
        box.addView(header(t("ov_top")));
        if (b7.isEmpty()) box.addView(ui.tv(t("ov_none_new"), 13, ui.sub));
        for (int i = 0; i < Math.min(5, b7.size()); i++) box.addView(fileRow(b7.get(i)), ui.gap());

        TextView n1 = ui.tv(t("ov_time_note"), 12, ui.sub);
        n1.setPadding(ui.dp(4), ui.dp(6), ui.dp(4), 0);
        box.addView(n1);
        TextView n2 = ui.tv(t("ov_hint"), 12, ui.sub);
        n2.setPadding(ui.dp(4), ui.dp(6), ui.dp(4), 0);
        box.addView(n2);
        return box;
    }

    private TextView header(String s) {
        TextView h = ui.tv(s, 15, ui.tx);
        h.setPadding(ui.dp(4), ui.dp(6), ui.dp(4), ui.dp(4));
        h.setTypeface(null, android.graphics.Typeface.BOLD);
        return h;
    }

    // ---------- file rows and dialogs ----------

    private View fileRow(final Scanner.Rec r) {
        LinearLayout c = ui.card();
        LinearLayout row = ui.row(c);
        TextView ic = ui.tv(Fmt.icon(Fmt.name(r.path)), 24, ui.tx);
        ic.setGravity(Gravity.CENTER);
        ic.setPadding(0, 0, ui.dp(8), 0);
        row.addView(ic, new LinearLayout.LayoutParams(ui.dp(44), Ui.WRAP));
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        TextView name = ui.tv(Fmt.name(r.path), 14, ui.tx);
        name.setSingleLine(true);
        name.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        TextView par = ui.tv(Fmt.parent(r.path), 11, ui.sub);
        par.setSingleLine(true);
        par.setEllipsize(TextUtils.TruncateAt.START);
        String own = ownerText(r.path);
        TextView meta = ui.tv((r.time > 0 ? ageText(r.time) : "-") + (own.isEmpty() ? "" : "  \u2022  " + own), 11, ui.sub);
        meta.setSingleLine(true);
        meta.setEllipsize(TextUtils.TruncateAt.END);
        col.addView(name);
        col.addView(par);
        col.addView(meta);
        row.addView(col, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
        TextView sz = ui.tv(Fmt.size(r.size), 14, r.size > 500_000_000L ? 0xFFEF5350 : ui.tx);
        sz.setTypeface(null, android.graphics.Typeface.BOLD);
        sz.setPadding(ui.dp(6), 0, 0, 0);
        row.addView(sz, new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP));
        c.setOnClickListener(v -> fileDialog(r));
        return c;
    }

    private void fileDialog(final Scanner.Rec r) {
        String own = ownerText(r.path);
        String msg = t("fd_path") + ": " + r.path + "\n\n"
                + t("fd_size") + ": " + Fmt.size(r.size) + "\n"
                + t("fd_modified") + ": " + Fmt.date(r.time) + "\n"
                + (r.time > 0 ? t("fd_age") + ": " + Fmt.age(System.currentTimeMillis() - r.time) + "\n" : "")
                + (own.isEmpty() ? "" : t("fd_owner") + ": " + own);
        ui.dlg(this).setTitle(Fmt.name(r.path)).setMessage(msg)
                .setPositiveButton(t("delete"), (d, w) -> confirmDelete(r))
                .setNeutralButton(t("copy_path"), (d, w) -> copy(r.path))
                .setNegativeButton(t("close"), null)
                .show();
    }

    private void confirmDelete(final Scanner.Rec r) {
        ui.dlg(this).setTitle(Fmt.name(r.path)).setMessage(t("del_msg") + "\n\n" + Fmt.size(r.size))
                .setPositiveButton(t("yes"), (d, w) -> {
                    File f = new File(r.path);
                    if (f.delete()) {
                        removeFromLists(r.path);
                        toast(t("deleted"));
                        renderTab();
                    } else {
                        toast(t("del_failed"));
                    }
                })
                .setNegativeButton(t("no"), null)
                .show();
    }

    private static void removeFromLists(String path) {
        for (List<Scanner.Rec> l : BIG) strip(l, path);
        strip(newestL, path);
    }

    private static void strip(List<Scanner.Rec> l, String path) {
        Iterator<Scanner.Rec> it = l.iterator();
        while (it.hasNext()) if (it.next().path.equals(path)) it.remove();
    }

    // ---------- Files ----------

    private View buildFiles() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        if (!Access.hasFiles(this) || root == null) {
            box.addView(ui.tv(t(scanning ? "ov_scanning" : "ov_start").replace("%d", "..."), 14, ui.sub));
            return box;
        }
        LinearLayout r1 = ui.row(box);
        for (int i = 0; i < 5; i++) {
            final int idx = i;
            r1.addView(chip(t("w_s" + i), fWin == i, v -> {
                fWin = idx;
                renderTab();
            }), ui.weighted());
        }
        LinearLayout r2 = ui.row(box);
        r2.addView(chip(t("f_size"), fSort == 0, v -> {
            fSort = 0;
            renderTab();
        }), ui.weighted());
        r2.addView(chip(t("f_time"), fSort == 1, v -> {
            fSort = 1;
            renderTab();
        }), ui.weighted());

        List<Scanner.Rec> list = new ArrayList<Scanner.Rec>();
        if (fSort == 0) {
            list.addAll(BIG.get(fWin));
        } else {
            long now = System.currentTimeMillis();
            for (Scanner.Rec r : newestL) {
                if (fWin == 4 || now - r.time <= Scanner.WIN[fWin]) list.add(r);
            }
        }
        long sum = 0;
        for (Scanner.Rec r : list) sum += r.size;
        TextView h = header(t("w_" + fWin));
        box.addView(h);
        if (list.isEmpty()) {
            box.addView(ui.tv(t("f_none"), 13, ui.sub));
            return box;
        }
        box.addView(ui.tv(String.format(Locale.US, t("f_total"), Fmt.size(sum), list.size()), 12, ui.sub));
        for (Scanner.Rec r : list) box.addView(fileRow(r), ui.gap());
        TextView n = ui.tv(String.format(Locale.US, t("f_trunc"), CAP), 11, ui.sub);
        n.setPadding(ui.dp(4), ui.dp(4), ui.dp(4), 0);
        box.addView(n);
        return box;
    }

    // ---------- Folders ----------

    private View buildFolders() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        if (!Access.hasFiles(this) || root == null || fCur == null) {
            box.addView(ui.tv(t("fo_need_scan"), 14, ui.sub));
            return box;
        }
        TextView path = ui.tv(fCur.parent == null ? t("fo_root") : fCur.path, 12, ui.sub);
        path.setSingleLine(true);
        path.setEllipsize(TextUtils.TruncateAt.START);
        box.addView(path, new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));

        LinearLayout top = ui.row(box);
        if (fCur.parent != null) {
            top.addView(ui.btn("\u2B06 " + t("fo_up"), Ui.GRAY, v -> {
                fCur = fCur.parent;
                foFiles = false;
                renderTab();
            }), ui.weighted());
        }
        top.addView(ui.btn(foFiles ? t("fo_folders") : t("fo_files"), Ui.BLUE, v -> {
            foFiles = !foFiles;
            renderTab();
        }), ui.weighted());
        box.addView(ui.tv(Fmt.size(fCur.size) + "  \u2022  " + String.format(Locale.US, t("fo_n"), fCur.files)
                + "  \u2022  +" + Fmt.size(fCur.add[1]) + " / 24 h", 13, ui.tx));

        if (foFiles) {
            LinearLayout r2 = ui.row(box);
            r2.addView(chip(t("f_size"), foFilesSort == 0, v -> {
                foFilesSort = 0;
                renderTab();
            }), ui.weighted());
            r2.addView(chip(t("f_time"), foFilesSort == 1, v -> {
                foFilesSort = 1;
                renderTab();
            }), ui.weighted());
            File[] fs = new File(fCur.path).listFiles();
            List<Scanner.Rec> l = new ArrayList<Scanner.Rec>();
            if (fs != null) {
                for (File f : fs) {
                    if (f.isDirectory()) continue;
                    Scanner.Rec r = new Scanner.Rec();
                    r.path = f.getPath();
                    r.size = f.length();
                    r.time = f.lastModified();
                    l.add(r);
                }
            }
            final int mode = foFilesSort;
            Collections.sort(l, new Comparator<Scanner.Rec>() {
                @Override
                public int compare(Scanner.Rec a, Scanner.Rec b) {
                    return mode == 0 ? Long.compare(b.size, a.size) : Long.compare(b.time, a.time);
                }
            });
            if (l.isEmpty()) box.addView(ui.tv(t("fo_empty"), 13, ui.sub));
            for (int i = 0; i < Math.min(400, l.size()); i++) box.addView(fileRow(l.get(i)), ui.gap());
            return box;
        }

        LinearLayout r2 = ui.row(box);
        r2.addView(chip(t("fo_sort_size"), foSort == 0, v -> {
            foSort = 0;
            renderTab();
        }), ui.weighted());
        r2.addView(chip(t("fo_sort_24h"), foSort == 1, v -> {
            foSort = 1;
            renderTab();
        }), ui.weighted());
        r2.addView(chip(t("fo_sort_7d"), foSort == 2, v -> {
            foSort = 2;
            renderTab();
        }), ui.weighted());
        List<Scanner.Dir> kids = new ArrayList<Scanner.Dir>(fCur.kids);
        final int sm = foSort;
        Collections.sort(kids, new Comparator<Scanner.Dir>() {
            @Override
            public int compare(Scanner.Dir a, Scanner.Dir b) {
                long ka = sm == 0 ? a.size : a.add[sm == 1 ? 1 : 2];
                long kb = sm == 0 ? b.size : b.add[sm == 1 ? 1 : 2];
                return Long.compare(kb, ka);
            }
        });
        if (kids.isEmpty()) box.addView(ui.tv(t("fo_empty"), 13, ui.sub));
        for (final Scanner.Dir k : kids) {
            LinearLayout c = ui.card();
            LinearLayout row = ui.row(c);
            TextView ic = ui.tv("\uD83D\uDCC1", 24, ui.tx);
            ic.setPadding(0, 0, ui.dp(8), 0);
            row.addView(ic, new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP));
            LinearLayout col = new LinearLayout(this);
            col.setOrientation(LinearLayout.VERTICAL);
            TextView nm = ui.tv(k.name, 15, ui.tx);
            nm.setSingleLine(true);
            nm.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            col.addView(nm);
            col.addView(ui.tv(String.format(Locale.US, t("fo_n"), k.files) + "  \u2022  +" + Fmt.size(k.add[1]) + " / 24 h  \u2022  +"
                    + Fmt.size(k.add[2]) + " / 7 d", 11, ui.sub));
            row.addView(col, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
            TextView sz = ui.tv(Fmt.size(k.size), 14, ui.tx);
            sz.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(sz, new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP));
            c.setOnClickListener(v -> {
                fCur = k;
                renderTab();
            });
            box.addView(c, ui.gap());
        }
        return box;
    }

    // ---------- Apps ----------

    private View buildApps() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        if (!AppSizes.supported()) {
            box.addView(ui.tv(t("ap_old"), 14, ui.sub));
            return box;
        }
        if (!Access.hasUsage(this)) {
            LinearLayout c = ui.card();
            TextView m = ui.tv(t("ap_need"), 13, ui.sub);
            m.setPadding(0, 0, 0, ui.dp(6));
            c.addView(m);
            c.addView(ui.btn(t("ap_open"), Ui.GREEN, v -> Access.openUsageSettings(this)),
                    new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
            box.addView(c, ui.gap());
            return box;
        }
        LinearLayout r = ui.row(box);
        r.addView(ui.btn(t("ap_scan"), Ui.GREEN, v -> scanApps()), ui.weighted());
        r.addView(chip(t("ap_by_size"), !appsByGrowth, v -> {
            appsByGrowth = false;
            renderTab();
        }), ui.weighted());
        r.addView(chip(t("ap_by_growth"), appsByGrowth, v -> {
            appsByGrowth = true;
            renderTab();
        }), ui.weighted());
        if (appsBusy) {
            box.addView(ui.tv(t("ap_scanning"), 13, ui.tx));
            return box;
        }
        if (!appsNote.isEmpty()) box.addView(ui.tv(appsNote, 12, 0xFFFFA726));
        if (apps == null) return box;
        if (apps.isEmpty()) {
            box.addView(ui.tv(t("ap_none"), 13, ui.sub));
            return box;
        }
        List<AppSizes.Item> l = new ArrayList<AppSizes.Item>(apps);
        AppSizes.sort(l, appsByGrowth);
        for (final AppSizes.Item it : l) {
            LinearLayout c = ui.card();
            LinearLayout row = ui.row(c);
            LinearLayout col = new LinearLayout(this);
            col.setOrientation(LinearLayout.VERTICAL);
            TextView nm = ui.tv(it.label, 15, ui.tx);
            nm.setSingleLine(true);
            nm.setEllipsize(TextUtils.TruncateAt.END);
            col.addView(nm);
            col.addView(ui.tv(t("ap_apk") + " " + Fmt.size(it.app) + "  \u2022  " + t("ap_data") + " " + Fmt.size(it.data)
                    + "  \u2022  " + t("ap_cache") + " " + Fmt.size(it.cache), 11, ui.sub));
            if (it.before >= 0 && it.total != it.before) {
                long d = it.total - it.before;
                col.addView(ui.tv((d > 0 ? "+" : "\u2212") + Fmt.size(Math.abs(d)), 12, d > 0 ? 0xFFEF5350 : 0xFF66BB6A));
            } else if (it.before < 0 && appsBaseTime > 0) {
                col.addView(ui.tv(t("ap_new"), 12, 0xFFFFA726));
            }
            row.addView(col, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
            TextView sz = ui.tv(Fmt.size(it.total), 15, ui.tx);
            sz.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(sz, new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP));
            c.setOnClickListener(v -> Access.openAppInfo(this, it.pkg));
            box.addView(c, ui.gap());
        }
        TextView n = ui.tv(t("ap_note"), 11, ui.sub);
        n.setPadding(ui.dp(4), ui.dp(4), ui.dp(4), 0);
        box.addView(n);
        return box;
    }

    private static long appsBaseTime = 0;

    private void scanApps() {
        if (appsBusy) return;
        appsBusy = true;
        renderTab();
        new Thread(() -> {
            final List<AppSizes.Item> items = AppSizes.scan(this);
            final long now = System.currentTimeMillis();
            String last = Store.snap(this, "last");
            String prev = Store.snap(this, "prev");
            long lastT = AppSizes.snapTime(last);
            String base;
            long baseT;
            boolean rotate;
            if (lastT == 0) {
                base = "";
                baseT = 0;
                rotate = true;
            } else if (now - lastT >= Scanner.H) {
                base = last;
                baseT = lastT;
                rotate = true;
            } else if (!prev.isEmpty()) {
                base = prev;
                baseT = AppSizes.snapTime(prev);
                rotate = false;
            } else {
                base = "";
                baseT = 0;
                rotate = false;
            }
            HashMap<String, Long> old = AppSizes.decode(base);
            for (AppSizes.Item it : items) {
                Long b = old.get(it.pkg);
                it.before = b == null ? -1 : b;
            }
            if (rotate) {
                if (lastT != 0) Store.setSnap(this, "prev", last);
                Store.setSnap(this, "last", AppSizes.encode(now, items));
            }
            final long bt = baseT;
            runOnUiThread(() -> {
                apps = items;
                appsBase(bt, now);
                appsBusy = false;
                renderTab();
            });
        }).start();
    }

    private void appsBase(long baseT, long now) {
        appsBaseTime = baseT;
        if (baseT == 0) appsNote = t("ap_first");
        else appsNote = String.format(Locale.US, t("ap_since"), Fmt.age(now - baseT));
    }

    // ---------- Settings ----------

    private View buildSettings() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);

        LinearLayout c1 = ui.card();
        c1.addView(ui.tv(t("st_lang"), 16, ui.tx));
        final RadioGroup rgL = new RadioGroup(this);
        String cur = Store.lang(this);
        addRadio(rgL, 101, "English", "en".equals(cur));
        addRadio(rgL, 102, "\u0627\u0631\u062F\u0648", "ur".equals(cur));
        addRadio(rgL, 103, "\u0627\u0644\u0639\u0631\u0628\u064A\u0629", "ar".equals(cur));
        c1.addView(rgL);
        box.addView(c1, ui.gap());

        LinearLayout c2 = ui.card();
        c2.addView(ui.tv(t("st_theme"), 16, ui.tx));
        final RadioGroup rgT = new RadioGroup(this);
        addRadio(rgT, 111, t("st_dark"), Store.dark(this));
        addRadio(rgT, 112, t("st_light"), !Store.dark(this));
        c2.addView(rgT);
        box.addView(c2, ui.gap());

        box.addView(ui.btn(t("st_save"), Ui.ORANGE, v -> {
            int id = rgL.getCheckedRadioButtonId();
            Store.setLang(this, id == 102 ? "ur" : (id == 103 ? "ar" : "en"));
            Store.setDark(this, rgT.getCheckedRadioButtonId() != 112);
            build();
            toast(t("st_saved"));
        }), ui.gap());

        LinearLayout c3 = ui.card();
        c3.addView(ui.tv(t("st_about"), 16, ui.tx));
        TextView body = ui.tv(t("st_about_text"), 13, ui.sub);
        body.setPadding(0, ui.dp(6), 0, 0);
        c3.addView(body);
        box.addView(c3, ui.gap());
        return box;
    }

    private void addRadio(RadioGroup g, int id, String text, boolean checked) {
        RadioButton rb = new RadioButton(this);
        rb.setId(id);
        rb.setText(text);
        ui.tint(rb);
        g.addView(rb);
        if (checked) rb.setChecked(true);
    }
}
