set sdkpath=.\TradeHero\TH_ANDROID\libs\baidu

mvn install:install-file -DgroupId=com.baidu -DartifactId=pushSDK -Dversion=3.2.0 -Dfile=%sdkpath%\armeabi\libbdpush_V1_0.so -Dpackaging=so -DgeneratePom=true -Dclassifier=armeabi

mvn install:install-file -DgroupId=com.baidu -DartifactId=pushSDK -Dversion=3.2.0 -Dfile=%sdkpath%\x86\libbdpush_V1_0.so -Dpackaging=so -DgeneratePom=true -Dclassifier=x86

mvn install:install-file -DgroupId=com.baidu -DartifactId=pushSDK -Dversion=3.2.0 -Dfile=%sdkpath%\mips\libbdpush_V1_0.so -Dpackaging=so -DgeneratePom=true -Dclassifier=mips

mvn install:install-file -DgroupId=com.baidu -DartifactId=pushSDK -Dversion=3.2.0 -Dfile=%sdkpath%\pushservice-3.2.0.jar -Dpackaging=jar -DgeneratePom=true
pause