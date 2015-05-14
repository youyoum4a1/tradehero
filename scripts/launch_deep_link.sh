#Debug version
adb shell am start -W -a android.intent.action.VIEW -d "tradehero://user/me" com.tradehero.th.dev

#Release version
adb shell am start -W -a android.intent.action.VIEW -d "tradehero://user/me" com.tradehero.th
