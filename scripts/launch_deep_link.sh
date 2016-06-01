#Debug version
adb shell am start -W -a android.intent.action.VIEW -d "tradehero://user/me" com.ayondo.academy.dev

#Release version
adb shell am start -W -a android.intent.action.VIEW -d "tradehero://user/me" com.ayondo.academy
