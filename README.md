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
- If there is any compilation error regarding java 1.7, go to File > Setting > Java Compiler and remove all in "Per module bytecode version"

#### Useful tip
- Disable unnecessary logcat error output by going to Setting - Apps - All and disable exchange service
- android-support-v4 is required by facebook module, it can be found under android sdk, inside folder named "extras".
- Enable break point for uncatched exception: Run > View Breakpoints > Check on Uncatched Exceptions

Development is conducted on the develop branch and the master is only used for release version.
We are following the guidelines described here: http://nvie.com/posts/a-successful-git-branching-model/
