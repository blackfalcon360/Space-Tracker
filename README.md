# Space Tracker

Finds which recent files and folders filled your phone storage. Java, no AndroidX, no libraries, **no internet permission**.
Package: `com.blackfalcon.spacetracker`. By: Black Falcon

## What it does
- **Overview**: storage bar (used/free), how much was added in the last hour / 24 hours / 7 days / 30 days, the biggest new file, top 5 biggest files of the week.
- **Files**: pick a period (1 h, 24 h, 7 d, 30 d, all) and sort by size or time. Tap a file for details, a guess of where it came from, copy path or delete.
- **Folders**: browse folders with their size and growth (+24 h / +7 d); "Files here" lists the files of a folder.
- **Apps** (Android 8+, needs Usage access): each app's size (app, data, cache) and how much it grew since the last scan (at least 1 hour earlier).
- **Settings**: language (English default, Urdu, Arabic), dark/light theme.

## Build
Upload the folder contents to GitHub; the "Build APK" workflow produces `SpaceTracker-APK` under the run's Artifacts.

## Permissions
- Android 11+: "All files access" (you allow it in a Settings screen the app opens).
- Android 7-9: Storage permission (asked at runtime).
- Usage access (optional, for the Apps tab) and the app list.
Nothing leaves the phone.

## Honest limits
- Android does not record which app wrote a file. "Probably from" is a guess from the folder name (for example `Android/media/com.whatsapp`).
- A file's date is its last-modified time. Restored or copied files can show an old date.
- Other apps' private data and `Android/data` cannot be read on Android 11+. Use the Apps tab for their size.
- Android 10 hides most non-media files from apps: results are incomplete there.
- The scan is a snapshot of the moment. After deleting, scan again for exact totals.
- Sizes use 1 GB = 1000 MB, like Android's own storage screen.
- Not compiled or tested on a device by the author. The scanner, size/age formatting and folder guessing were tested on a computer.
- Xiaomi/Oppo/Vivo/Huawei: nothing runs in the background, so no Autostart setting is needed.

## اردو (مختصر)
- یہ ایپ دکھاتی ہے کہ حال ہی میں کون سی فائلیں اور فولڈر آپ کا اسٹوریج بھر گئے۔
- اینڈرائیڈ یہ نہیں بتاتا کہ فائل کس ایپ نے بنائی؛ ایپ فولڈر کے نام سے اندازہ لگاتی ہے۔
- اینڈرائیڈ 11+ پر "تمام فائلوں تک رسائی" دینی ہوتی ہے۔ دوسری ایپس کا نجی ڈیٹا ایپس ٹیب میں سائز کے طور پر نظر آتا ہے۔
- انٹرنیٹ کی اجازت نہیں؛ کچھ فون سے باہر نہیں جاتا۔
