package com.tradehero.common.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.tradehero.th.base.Application;
import com.tradehero.th.utils.Constants;
import timber.log.Timber;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class THLog
{
    public static void showDeveloperKeyHash()
    {
        if (!Constants.RELEASE)
        {
            try
            {
                PackageInfo info = Application.context()
                        .getPackageManager()
                        .getPackageInfo("com.tradehero.th", PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures)
                {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Timber.d("KeyHash: %s", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            }
            catch (NullPointerException | PackageManager.NameNotFoundException | NoSuchAlgorithmException e)
            {
                Timber.d("KeyHash Error", e.getMessage());
            }
        }
    }


    private final static boolean isDebug = true;
    private final static String TAG = "abc";


    public static void d(String logStr){
        if(!isDebug){
            return;
        }
        if(TextUtils.isEmpty(logStr)){
            Log.d(TAG, "");
            return;
        }
        Log.d(TAG, logStr);
    }
}
