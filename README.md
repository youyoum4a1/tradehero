TradeHero Android
=================

[![Build Status](https://magnum.travis-ci.com/TradeHero/TH_ANDROID.svg?token=vUqojFu6gnzhdpmzXo1z&branch=develop)](https://magnum.travis-ci.com/TradeHero/TH_ANDROID)

#### Checkout source code
Clone the repository with all the submodules 
```
git clone git@github.com:TradeHero/TH_ANDROID.git --recursive
git checkout origin/develop --track
```

#### Setting up workspace
- Install Java Development Kit (JDK) 7 or later.
- Install Android Studio IDE
- Get latest version of Android SDK from Google
- Setup all configuration ask prompted for JAVA & Android, after that, download Android 4.0 (API 14) from SDK Manager (Tool > Android > SDK Manager)
- To make it easier working with Dagger (dependency injection module), install this plugin: https://github.com/square/dagger-intellij-plugin
- To build for release, you need Crashlytics plugin. Download it from https://www.crashlytics.com/downloads/intellij and install from disk.
- DO NOT publish app to Google Play before obfuscating it with ProGuard!!! To know how, read the proguard help page from Wiki.
- Use common adb key, otherwise you will need to add your own keyhash to list of keyhashes on facebook developer dashboard, to do it, run
`scripts\install_common_adbkey.bat` (Windows) or `scripts/install_common_adbkey.sh` (Linux & Mac)

#### Useful tip
- Disable unnecessary logcat error output by going to Setting - Apps - All and disable exchange service
- android-support-v4 is required by facebook module, it can be found under android sdk, inside folder named "extras".
- Enable break point for uncatched exception: Run > View Breakpoints > Check on Uncatched Exceptions
- Use this simulator: http://www.genymotion.com/ instead of the built-in.

Development is conducted on the develop branch and the master is only used for release version.
We are following the guidelines described here: http://nvie.com/posts/a-successful-git-branching-model/
