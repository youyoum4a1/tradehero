Getting started:
================
#### Checkout source code
Clone the repository with all the submodules 
```
git clone git@github.com:TradeHero/TH_ANDROID.git --recursive
git checkout origin/develop --track
```

#### Setting up workspace
- Install Java Development Kit (JDK) 7 or later.
- Install IntelliJ IDE (all Android and maven plugins are required)
- Enable Maven auto import when you are asked to do that or go to Settings -> search for maven ->  "Import Maven ..."
- Get android sdk latest version from Google
- With each maven module which referencing an outside module (you should see something like ```<relativePath>../pom.xml</relativePath>``` in pom.xml of that project), go to referenced project and mark it as a maven project as well.
- Setup all configuration ask prompted for JAVA & Android, after that, download Android 4.0 (API 14) from SDK Manager (Tool > Android > SDK Manager)
- To make it easier working with Dagger (dependency injection module), install this plugin: https://github.com/square/dagger-intellij-plugin
- To build for release, you need Crashlytics plugin. Download it from https://www.crashlytics.com/downloads/intellij and install from disk.
- DO NOT publish app to Google Play before obfuscating it with ProGuard!!! To know how, read the proguard help page from Wiki.
- If the `parent` project has an Android facet, remove the facet.
- If a submodule, such as `facebook-android-sdk` is not detected as a Maven project, right-click on its `pom.xml` file and choose `Add as Maven project`.

#### Useful tip
- Disable unnecessary logcat error output by going to Setting - Apps - All and disable exchange service
- android-support-v4 is required by facebook module, it can be found under android sdk, inside folder named "extras".
- Enable break point for uncatched exception: Run > View Breakpoints > Check on Uncatched Exceptions
- Use this simulator: http://www.genymotion.com/ instead of the built-in.

Development is conducted on the develop branch and the master is only used for release version.
We are following the guidelines described here: http://nvie.com/posts/a-successful-git-branching-model/
