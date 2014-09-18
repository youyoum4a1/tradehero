# Example to unzip apk
./baksmali /Users/xavier/Documents/Projects/TH_ANDROID2/TradeHero/TH_ANDROID/build/outputs/apk/app-amazon-debug.apk -o /Users/xavier/Documents/Temp/apktool/amazon2

# Example to count methods 
find /Users/xavier/Documents/Temp/apktool/amazon2/  -type d -print -exec sh -c "./smali {} -o {}/classes.dex && sh -c \"/Users/xavier/Documents/Projects/adt-bundle-mac-x86_64-20131030/sdk/build-tools/20.0.0/dexdump -f {}/classes.dex | grep method_ids_size\"" \; > method_count_amazon_2.txt 