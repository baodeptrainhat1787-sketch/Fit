# Build APK in GitHub Actions (no Android SDK needed on your phone)

This project includes `.github/workflows/android.yml` to build a debug APK in GitHub's hosted runner.

1. Create a GitHub repository and upload the contents of this project (the files inside `FitPlan`, not the ZIP itself).
2. Open the repository's **Actions** tab and enable Actions if prompted.
3. Select **Build FitPlan APK** and press **Run workflow** (or push to `main`/`master`).
4. Wait for the workflow to finish successfully.
5. Open the completed workflow run and download the artifact named **FitPlan-debug-apk**.
6. Extract the downloaded artifact to get `app-debug.apk`, then transfer it to Android and install it. Android may ask permission to install unknown apps.

This produces a debug APK, not a Play Store release-signed APK. The current execution environment has no Gradle or Android SDK, so the APK cannot be built and validated directly here.
