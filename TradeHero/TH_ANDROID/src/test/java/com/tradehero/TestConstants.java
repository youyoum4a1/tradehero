package com.tradehero;

public class TestConstants
{
    public static final boolean IS_INTELLIJ = System.getProperties().containsKey("idea.launcher.bin.path");
    public static final String BASE_APP_FOLDER = IS_INTELLIJ ? "./TradeHero/TH_ANDROID/" : "./";
    public static final String JSON_MIME_UTF8 = "application/json; charset=utf-8";

    public static final String LIBRARIES_GENERATED_FOLDER = IS_INTELLIJ ? "gen-external-apklibs" : "target/unpack/apklibs";
    public static final String MANIFEST_PATH = BASE_APP_FOLDER + "AndroidManifest.xml";
    public static final String RES_PATH = BASE_APP_FOLDER + "res";
    public static final String ASSETS_PATH = BASE_APP_FOLDER + "assets";
}
