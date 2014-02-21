package com.tradehero.common.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import com.tradehero.th.base.Application;
import com.tradehero.th.utils.Constants;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: thonguyen Date: 6/19/13 Time: 3:02 PM Special for debugging purpose
 */
public class THLog
{
    private static final String PREFIX = "TradeHero-";

    /**
     * Use Timber instead
     */
    @Deprecated
    public static void d(String tag, String msg)
    {
        if (!Constants.RELEASE)
        {
            Log.d(PREFIX + tag, msg);
        }
    }

    /**
     * Use Timber instead
     */
    @Deprecated
    public static void i(String tag, String msg)
    {
        if (!Constants.RELEASE)
        {
            Log.i(PREFIX + tag, msg);
        }
    }

    /**
     * Use Timber instead
     */
    @Deprecated
    public static void w(String tag, String msg)
    {
        if (!Constants.RELEASE)
        {
            Log.w(PREFIX + tag, msg);
        }
    }

    /**
     * Use Timber instead
     */
    @Deprecated
    public static void e(String tag, String msg, Throwable ex)
    {
        if (!Constants.RELEASE)
        {
            String prefixedTag = PREFIX + tag;
            Log.e(prefixedTag, msg, ex);
        }
    }

    /**
     * Use Timber instead
     */
    @Deprecated
    public static void d(String tag, String msg, long startNanoTime)
    {
        if (!Constants.RELEASE)
        {
            long finish = System.nanoTime();
            float seconds = (finish - startNanoTime) / 1000000f; //for milliseconds

            Log.d(tag, String.format("%,.3f milliseconds for %s", seconds, msg));
        }
    }

    /** Display KeyHash which is required by Facebook Application */
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
}
