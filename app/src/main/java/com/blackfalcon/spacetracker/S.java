package com.blackfalcon.spacetracker;

import android.content.Context;
import java.util.HashMap;

/** All UI text in English (0), Urdu (1), Arabic (2). */
final class S {
    private S() {}

    private static final HashMap<String, String[]> M = new HashMap<String, String[]>();

    private static void a(String k, String en, String ur, String ar) {
        M.put(k, new String[]{en, ur, ar});
    }

    static {
        a("app_name", "Space Tracker", "اسپیس ٹریکر", "متتبع المساحة");
        a("tab_overview", "Overview", "خلاصہ", "نظرة عامة");
        a("tab_files", "Files", "فائلیں", "الملفات");
        a("tab_folders", "Folders", "فولڈر", "المجلدات");
        a("tab_apps", "Apps", "ایپس", "التطبيقات");
        a("tab_settings", "Settings", "سیٹنگز", "الإعدادات");
        a("by", "By: Black Falcon \uD83E\uDD85", "By: Black Falcon \uD83E\uDD85", "By: Black Falcon \uD83E\uDD85");
        a("back", "Back", "واپس", "رجوع");
        a("yes", "Yes", "ہاں", "نعم");
        a("no", "No", "نہیں", "لا");
        a("close", "Close", "بند کریں", "إغلاق");
        a("exit_title", "Exit", "باہر نکلیں", "خروج");
        a("exit_msg", "Do you want to exit the app?", "کیا آپ ایپ سے باہر نکلنا چاہتے ہیں؟", "هل تريد الخروج من التطبيق؟");
        a("error", "Something went wrong.", "کچھ غلط ہو گیا۔", "حدث خطأ ما.");
        a("copy_path", "Copy path", "راستہ کاپی کریں", "نسخ المسار");
        a("copied", "Copied", "کاپی ہو گیا", "تم النسخ");
        a("delete", "Delete", "حذف کریں", "حذف");
        a("del_msg", "Delete this file permanently? This cannot be undone.", "کیا اس فائل کو ہمیشہ کے لیے حذف کریں؟ یہ واپس نہیں آئے گی۔", "حذف هذا الملف نهائيًا؟ لا يمكن التراجع.");
        a("deleted", "Deleted", "حذف ہو گیا", "تم الحذف");
        a("del_failed", "Could not delete this file (no permission or in use).", "یہ فائل حذف نہیں ہو سکی (اجازت نہیں یا استعمال میں ہے)۔", "تعذر حذف هذا الملف (لا إذن أو قيد الاستخدام).");
        a("perm_title", "Storage access needed", "اسٹوریج تک رسائی چاہیے", "يلزم الوصول إلى التخزين");
        a("perm_msg", "To find the files that fill your storage, Android needs \"All files access\" for this app (Android 11 and newer) or the storage permission (older Android). The app only reads file names, sizes and dates on your phone. Nothing is sent anywhere: the app has no internet permission.", "جو فائلیں آپ کا اسٹوریج بھر رہی ہیں انہیں ڈھونڈنے کے لیے اینڈرائیڈ کو اس ایپ کے لیے \"تمام فائلوں تک رسائی\" (اینڈرائیڈ 11 اور نئے) یا اسٹوریج اجازت (پرانے اینڈرائیڈ) چاہیے۔ ایپ صرف آپ کے فون پر فائلوں کے نام، سائز اور تاریخیں پڑھتی ہے۔ کچھ کہیں نہیں بھیجا جاتا: ایپ کے پاس انٹرنیٹ کی اجازت نہیں۔", "للعثور على الملفات التي تملأ التخزين يحتاج أندرويد إلى \"الوصول إلى جميع الملفات\" لهذا التطبيق (أندرويد 11 وما بعده) أو إذن التخزين (الإصدارات الأقدم). يقرأ التطبيق أسماء الملفات وأحجامها وتواريخها على هاتفك فقط. لا يُرسل شيء: التطبيق بلا إذن إنترنت.");
        a("perm_btn", "Allow access", "رسائی دیں", "السماح بالوصول");
        a("perm_back", "After allowing, come back to this app.", "اجازت دینے کے بعد اس ایپ میں واپس آئیں۔", "بعد السماح، عد إلى هذا التطبيق.");
        // overview
        a("ov_storage", "Phone storage", "فون اسٹوریج", "تخزين الهاتف");
        a("ov_used", "Used", "استعمال شدہ", "مستخدم");
        a("ov_free", "Free", "خالی", "متاح");
        a("ov_added", "Added to storage recently", "حال ہی میں اسٹوریج میں شامل ہوا", "أُضيف مؤخرًا إلى التخزين");
        a("w_0", "Last hour", "پچھلا گھنٹہ", "آخر ساعة");
        a("w_1", "Last 24 hours", "پچھلے 24 گھنٹے", "آخر 24 ساعة");
        a("w_2", "Last 7 days", "پچھلے 7 دن", "آخر 7 أيام");
        a("w_3", "Last 30 days", "پچھلے 30 دن", "آخر 30 يومًا");
        a("w_4", "All time", "ہمیشہ", "كل الوقت");
        a("w_s0", "1 h", "1 گھنٹہ", "1 س");
        a("w_s1", "24 h", "24 گھنٹے", "24 س");
        a("w_s2", "7 d", "7 دن", "7 أيام");
        a("w_s3", "30 d", "30 دن", "30 يومًا");
        a("w_s4", "All", "سب", "الكل");
        a("ov_big_new", "Biggest new file (last 24 hours)", "سب سے بڑی نئی فائل (پچھلے 24 گھنٹے)", "أكبر ملف جديد (آخر 24 ساعة)");
        a("ov_big_week", "Biggest new file (last 7 days)", "سب سے بڑی نئی فائل (پچھلے 7 دن)", "أكبر ملف جديد (آخر 7 أيام)");
        a("ov_none_new", "No new files found in this period.", "اس مدت میں کوئی نئی فائل نہیں ملی۔", "لا ملفات جديدة في هذه الفترة.");
        a("ov_top", "Biggest files added in the last 7 days", "پچھلے 7 دن میں شامل ہونے والی سب سے بڑی فائلیں", "أكبر الملفات المضافة في آخر 7 أيام");
        a("ov_scan", "Scan storage", "اسٹوریج اسکین کریں", "افحص التخزين");
        a("ov_scanning", "Scanning... %d files", "اسکین ہو رہا ہے... %d فائلیں", "جارٍ الفحص... %d ملف");
        a("ov_scanned", "Scanned %d files (%s) in %d s", "%d فائلیں (%s) %d سیکنڈ میں اسکین ہوئیں", "تم فحص %d ملف (%s) في %d ث");
        a("ov_stop", "Stop", "روکیں", "إيقاف");
        a("ov_hint", "Android does not let apps read other apps' private data (the Android/data folders and apps' internal storage). If the files here do not explain the missing space, open the Apps tab.", "اینڈرائیڈ ایپس کو دوسری ایپس کا نجی ڈیٹا (Android/data فولڈر اور ایپس کا اندرونی اسٹوریج) پڑھنے نہیں دیتا۔ اگر یہاں کی فائلیں غائب جگہ کی وضاحت نہ کریں تو ایپس ٹیب دیکھیں۔", "لا يسمح أندرويد للتطبيقات بقراءة بيانات التطبيقات الأخرى الخاصة (مجلدات Android/data والتخزين الداخلي للتطبيقات). إن لم تفسّر الملفات هنا المساحة المفقودة فافتح تبويب التطبيقات.");
        a("ov_start", "Tap \"Scan storage\" to start.", "شروع کرنے کے لیے \"اسٹوریج اسکین کریں\" دبائیں۔", "اضغط \"افحص التخزين\" للبدء.");
        a("ov_time_note", "A file's date is its last-modified time. Files copied or restored from a backup can show their original date, so a new file may look old.", "فائل کی تاریخ اس کے آخری ترمیم کا وقت ہے۔ کاپی یا بیک اپ سے بحال فائلیں اپنی اصل تاریخ دکھا سکتی ہیں، اس لیے نئی فائل پرانی لگ سکتی ہے۔", "تاريخ الملف هي وقت آخر تعديل. الملفات المنسوخة أو المستعادة قد تُظهر تاريخها الأصلي فقد يبدو الملف الجديد قديمًا.");
        // files
        a("f_size", "By size", "سائز کے مطابق", "حسب الحجم");
        a("f_time", "By time", "وقت کے مطابق", "حسب الوقت");
        a("f_none", "Nothing in this period.", "اس مدت میں کچھ نہیں۔", "لا شيء في هذه الفترة.");
        a("f_total", "Total of the files listed: %s in %d files", "درج فائلوں کا کل: %s، %d فائلوں میں", "مجموع الملفات المعروضة: %s في %d ملف");
        a("f_trunc", "Only the biggest/newest %d are kept per list.", "ہر فہرست میں صرف سب سے بڑی/نئی %d رکھی جاتی ہیں۔", "يُحتفظ فقط بأكبر/أحدث %d في كل قائمة.");
        a("fd_path", "Path", "راستہ", "المسار");
        a("fd_size", "Size", "سائز", "الحجم");
        a("fd_modified", "Modified", "ترمیم", "التعديل");
        a("fd_age", "Age", "عمر", "العمر");
        a("fd_owner", "Probably from", "غالباً کہاں سے", "على الأرجح من");
        a("age_ago", "%s ago", "%s پہلے", "قبل %s");
        // owner keys
        a("o_pkg", "App: %s", "ایپ: %s", "التطبيق: %s");
        a("o_whatsapp", "WhatsApp", "واٹس ایپ", "واتساب");
        a("o_telegram", "Telegram", "ٹیلیگرام", "تيليجرام");
        a("o_instagram", "Instagram", "انسٹاگرام", "إنستغرام");
        a("o_snapchat", "Snapchat", "اسنیپ چیٹ", "سناب شات");
        a("o_tiktok", "TikTok", "ٹک ٹاک", "تيك توك");
        a("o_facebook", "Facebook", "فیس بک", "فيسبوك");
        a("o_shareit", "SHAREit", "SHAREit", "SHAREit");
        a("o_xender", "Xender", "Xender", "Xender");
        a("o_bluetooth", "Bluetooth transfer", "بلوٹوتھ ٹرانسفر", "نقل بلوتوث");
        a("o_camera", "Camera (photos and videos)", "کیمرہ (تصاویر اور ویڈیوز)", "الكاميرا (صور وفيديو)");
        a("o_screenshots", "Screenshots", "اسکرین شاٹس", "لقطات الشاشة");
        a("o_recordings", "Recordings", "ریکارڈنگز", "التسجيلات");
        a("o_downloads", "Downloads (browser or an app)", "ڈاؤن لوڈز (براؤزر یا ایپ)", "التنزيلات (متصفح أو تطبيق)");
        a("o_music", "Music", "موسیقی", "الموسيقى");
        a("o_movies", "Movies and videos", "فلمیں اور ویڈیوز", "الأفلام والفيديو");
        a("o_thumbs", "Gallery thumbnails (cache)", "گیلری تھمب نیل (کیش)", "الصور المصغرة (ذاكرة مؤقتة)");
        a("o_documents", "Documents", "دستاویزات", "المستندات");
        a("o_cache", "Cache or temporary files", "کیش یا عارضی فائلیں", "ملفات مؤقتة");
        // folders
        a("fo_root", "Phone storage", "فون اسٹوریج", "تخزين الهاتف");
        a("fo_up", "Up", "اوپر", "أعلى");
        a("fo_files", "Files here", "یہاں کی فائلیں", "ملفات هنا");
        a("fo_folders", "Folders", "فولڈر", "المجلدات");
        a("fo_sort_size", "Size", "سائز", "الحجم");
        a("fo_sort_24h", "Added 24 h", "24 گھنٹے میں شامل", "أُضيف 24 س");
        a("fo_sort_7d", "Added 7 d", "7 دن میں شامل", "أُضيف 7 أيام");
        a("fo_empty", "Empty or not readable.", "خالی یا پڑھا نہیں جا سکتا۔", "فارغ أو غير قابل للقراءة.");
        a("fo_n", "%d files", "%d فائلیں", "%d ملف");
        a("fo_need_scan", "Scan the storage first (Overview tab).", "پہلے اسٹوریج اسکین کریں (خلاصہ ٹیب)۔", "افحص التخزين أولاً (تبويب نظرة عامة).");
        // apps
        a("ap_old", "Per-app sizes need Android 8 or newer.", "ہر ایپ کے سائز کے لیے اینڈرائیڈ 8 یا نیا چاہیے۔", "أحجام التطبيقات تحتاج أندرويد 8 أو أحدث.");
        a("ap_need", "To read each app's size, Android needs \"Usage access\" for this app. Open the settings, find Space Tracker and turn it on.", "ہر ایپ کا سائز پڑھنے کے لیے اینڈرائیڈ کو اس ایپ کے لیے \"Usage access\" چاہیے۔ سیٹنگز کھولیں، Space Tracker ڈھونڈیں اور آن کریں۔", "لقراءة حجم كل تطبيق يحتاج أندرويد إلى \"الوصول إلى بيانات الاستخدام\" لهذا التطبيق. افتح الإعدادات وابحث عن Space Tracker وفعّله.");
        a("ap_open", "Open settings", "سیٹنگز کھولیں", "افتح الإعدادات");
        a("ap_scan", "Scan apps", "ایپس اسکین کریں", "افحص التطبيقات");
        a("ap_by_size", "By size", "سائز کے مطابق", "حسب الحجم");
        a("ap_by_growth", "By growth", "اضافے کے مطابق", "حسب الزيادة");
        a("ap_total", "Total", "کل", "المجموع");
        a("ap_data", "Data", "ڈیٹا", "البيانات");
        a("ap_cache", "Cache", "کیش", "الذاكرة المؤقتة");
        a("ap_apk", "App", "ایپ", "التطبيق");
        a("ap_since", "since the earlier scan (%s ago)", "پچھلے اسکین کے بعد (%s پہلے)", "منذ الفحص السابق (قبل %s)");
        a("ap_new", "new app", "نئی ایپ", "تطبيق جديد");
        a("ap_first", "First scan saved. Scan again later to see which app grew.", "پہلا اسکین محفوظ ہو گیا۔ کون سی ایپ بڑھی یہ دیکھنے کے لیے بعد میں دوبارہ اسکین کریں۔", "تم حفظ الفحص الأول. افحص مرة أخرى لاحقًا لرؤية التطبيق الذي زاد.");
        a("ap_note", "Cache can be cleared in App info > Storage. Growth is compared with the last scan that is at least 1 hour old. Tap an app to open its App info.", "کیش ایپ انفو > اسٹوریج میں صاف ہوتا ہے۔ اضافہ اس پچھلے اسکین سے ملایا جاتا ہے جو کم از کم 1 گھنٹہ پرانا ہو۔ ایپ پر ٹیپ کریں تو اس کی ایپ انفو کھلے گی۔", "يمكن مسح الذاكرة المؤقتة من معلومات التطبيق > التخزين. تُقارن الزيادة بآخر فحص مضى عليه ساعة على الأقل. اضغط على تطبيق لفتح معلوماته.");
        a("ap_scanning", "Reading app sizes...", "ایپس کے سائز پڑھے جا رہے ہیں...", "جارٍ قراءة أحجام التطبيقات...");
        a("ap_none", "No app sizes could be read.", "کسی ایپ کا سائز نہیں پڑھا جا سکا۔", "تعذر قراءة أحجام التطبيقات.");
        // settings
        a("st_lang", "Language", "زبان", "اللغة");
        a("st_theme", "Theme", "تھیم", "المظهر");
        a("st_dark", "Dark", "ڈارک", "داكن");
        a("st_light", "Light", "لائٹ", "فاتح");
        a("st_save", "Save", "محفوظ کریں", "حفظ");
        a("st_saved", "Saved", "محفوظ ہو گیا", "تم الحفظ");
        a("st_about", "About and limits", "تعارف اور حدود", "حول والقيود");
        a("st_about_text", "Space Tracker scans your shared storage once and shows which files and folders were added or changed recently, the biggest files, folder sizes and, with Usage access, each app's size and growth. Sizes use 1 GB = 1000 MB, like Android's storage screen.\n\nLimits: Android does not record which app wrote a file, so the \"probably from\" line is a guess from the folder name. Other apps' private data and the Android/data folders cannot be read on Android 11 and newer. On Android 10, Android hides most non-media files from apps, so the results are incomplete. The scan is a snapshot: delete files, then scan again to see the new totals.\n\nPrivacy: no internet permission; nothing leaves your phone.", "اسپیس ٹریکر آپ کے مشترکہ اسٹوریج کو ایک بار اسکین کر کے دکھاتا ہے کہ کون سی فائلیں اور فولڈر حال ہی میں شامل یا تبدیل ہوئے، سب سے بڑی فائلیں، فولڈروں کے سائز، اور Usage access کے ساتھ ہر ایپ کا سائز اور اضافہ۔ سائز 1 GB = 1000 MB کے حساب سے ہیں، جیسے اینڈرائیڈ کی اسٹوریج اسکرین۔\n\nحدود: اینڈرائیڈ یہ نہیں لکھتا کہ فائل کس ایپ نے بنائی، اس لیے \"غالباً کہاں سے\" فولڈر کے نام سے اندازہ ہے۔ اینڈرائیڈ 11 اور نئے میں دوسری ایپس کا نجی ڈیٹا اور Android/data فولڈر نہیں پڑھے جا سکتے۔ اینڈرائیڈ 10 زیادہ تر غیر میڈیا فائلیں ایپس سے چھپاتا ہے، اس لیے نتائج نامکمل ہوں گے۔ اسکین ایک عکس ہے: فائلیں حذف کر کے نئے کل دیکھنے کے لیے دوبارہ اسکین کریں۔\n\nپرائیویسی: انٹرنیٹ کی اجازت نہیں؛ کچھ فون سے باہر نہیں جاتا۔", "يفحص متتبع المساحة تخزينك المشترك مرة واحدة ويعرض الملفات والمجلدات التي أُضيفت أو تغيّرت مؤخرًا وأكبر الملفات وأحجام المجلدات، ومع إذن بيانات الاستخدام حجم كل تطبيق وزيادته. الأحجام بحساب 1 GB = 1000 MB مثل شاشة التخزين في أندرويد.\n\nالقيود: لا يسجل أندرويد أي تطبيق كتب الملف لذا سطر \"على الأرجح من\" تخمين من اسم المجلد. لا يمكن قراءة بيانات التطبيقات الأخرى الخاصة ومجلدات Android/data في أندرويد 11 وما بعده. في أندرويد 10 تُخفى معظم الملفات غير الوسائط عن التطبيقات فتكون النتائج ناقصة. الفحص لقطة: احذف الملفات ثم افحص مجددًا لرؤية المجاميع الجديدة.\n\nالخصوصية: لا إذن إنترنت؛ لا يغادر شيء هاتفك.");
    }

    static String t(Context c, String key) {
        String[] v = M.get(key);
        if (v == null) return key;
        return v[Store.langIdx(c)];
    }
}
