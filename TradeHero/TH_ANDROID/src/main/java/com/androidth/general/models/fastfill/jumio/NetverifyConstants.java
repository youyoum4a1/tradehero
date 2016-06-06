package com.androidth.general.models.fastfill.jumio;

import android.util.Base64;

import com.androidth.general.BuildConfig;


public class NetverifyConstants
{
    public static final String NETVERIFY_END_POINT = "https://netverify.com/api/netverify/v2";
    public static final String NET_VERIFY_MERCHANT_API_TOKEN = "c4ed0584-b618-4311-b8c8-11ec5f36d47b";
    public static final String NET_VERIFY_ACTIVE_API_SECRET = "EvMD7WCL98sErCZvEezDReV8dZuqbzaU";

    public static final String NETVERIFY_AUTH_HEADER = "Basic " + Base64.encodeToString(
            String.format("%s:%s", NET_VERIFY_MERCHANT_API_TOKEN, NET_VERIFY_ACTIVE_API_SECRET).getBytes(),
            Base64.NO_WRAP);
    public static final String NETVERIFY_USER_AGENT = "MyHero " + BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_CODE;
}
