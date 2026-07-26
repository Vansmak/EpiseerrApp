# Episeerr Android App

Android TV / mobile client for [Episeerr](https://github.com/vansmak/episeerr). Optional companion app — Episeerr runs fully without it; this just gives you rule management, pending approvals, and service config from your phone or TV.

## Building

APKs are automatically built via GitHub Actions on every push to `main`. You can also trigger a build manually from the Actions tab — no secrets or signing setup required.

---

## Download the Built APK

1. Go to the repo on GitHub → **Actions** tab
2. Click the latest **Build Episeerr APK** workflow run
3. Scroll to **Artifacts** at the bottom
4. Download **Episeerr-debug**
5. Extract the zip — the APK is inside

---

## Install on Android TV or Mobile

### Sideloading on Android TV

1. On your Android TV, enable **Developer options**:
   - Go to **Settings → Device Preferences → About**
   - Click **Build** 7 times to unlock developer options
   - Go back to **Settings → Device Preferences → Developer options**
   - Enable **USB debugging** and **Unknown sources** (or **Install unknown apps**)

2. Transfer the APK to the TV:
   - Via USB drive: copy APK to a USB stick and open it with a file manager app (e.g. FX File Explorer)
   - Via ADB over network:
     ```bash
     adb connect <tv-ip-address>
     adb install app-debug.apk
     ```
   - Via file sharing: upload to Google Drive / Dropbox and download on the TV

### Sideloading on Android Mobile

1. On your phone, go to **Settings → Apps → Special app access → Install unknown apps**
2. Allow your file manager or browser to install unknown apps
3. Transfer the APK to the device and tap it to install
