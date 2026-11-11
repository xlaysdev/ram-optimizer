# RAM Optimizer

A lightweight Android device monitor built with Kotlin and Jetpack Compose.

## Current features

- View total, used, and available system RAM.
- View internal storage usage and free space.
- Refresh device statistics on demand.
- Simple native Android interface.

## Build an APK with GitHub Actions

Every push to `main` starts the **Build Android APK** workflow.

1. Open the repository's **Actions** tab.
2. Select the latest successful **Build Android APK** run.
3. Under **Artifacts**, download `ram-optimizer-debug-apk`.
4. Extract the ZIP and install `app-debug.apk` on your Android device.

You can also run the workflow manually with **Actions → Build Android APK → Run workflow**.

## Build locally

Requirements: JDK 17, Android SDK, and Gradle 8.9.

```bash
gradle assembleDebug
```

The APK is created at `app/build/outputs/apk/debug/app-debug.apk`.

## Important Android behavior

Android controls background process memory. This app reports system RAM and storage information; it does not claim to magically free RAM, force-stop other apps, or clear private app caches.

## Tech stack

- Kotlin
- Jetpack Compose
- Material 3
- Android Gradle Plugin 8.7.3
