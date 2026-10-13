# FitPlan Android

Android project for a Vietnamese offline-first fitness and nutrition planner.

## Included
- WebView app loads `app/src/main/assets/index.html` from local assets (no network required for the app content).
- Daily habit checklist saved per date in WebView local storage.
- Seven-day completion overview.
- Android daily workout and bedtime notifications, with reboot rescheduling.

## Build APK
Requirements: Android Studio (recommended), JDK 17, Android SDK Platform 35, Android Gradle Plugin 8.7.3.

1. Open this folder (`FitPlan`) in Android Studio.
2. Allow Gradle sync and install the requested Android SDK if prompted.
3. Choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. Debug APK should appear at `app/build/outputs/apk/debug/app-debug.apk`.

## Notes
- On Android 13+, grant notification permission when prompted.
- Android 12+ may use inexact fallback reminders if exact-alarm access is not granted; device battery settings may also affect delivery.
- Checklist and weekly progress are local to the app/browser storage on this device. Clearing app data removes them.
- No account, analytics, or network service is used by this project.
