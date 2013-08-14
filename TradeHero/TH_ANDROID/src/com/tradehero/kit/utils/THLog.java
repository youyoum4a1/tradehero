package com.tradehero.kit.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import com.tradehero.th.application.App;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA. User: thonguyen Date: 6/19/13 Time: 3:02 PM Special for debuging
 * purpose
 */
public class THLog
{
    private static final String PREFIX = "TradeHero-";

    public static void d(String tag, String msg)
    {
        Log.d(PREFIX + tag, msg);
    }

    public static void i(String tag, String msg)
    {
        Log.i(PREFIX + tag, msg);
    }

    public static void e(String tag, String message, Exception ex)
    {
        Log.e(PREFIX + tag, message, ex);
    }

    /** Display KeyHash which is required by Facebook Application */
    public static void showDeveloperKeyHash()
    {
        try
        {
            PackageInfo info = App.context()
                    .getPackageManager()
                    .getPackageInfo("com.tradehero.fh", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                THLog.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            THLog.d("KeyHash Error", e.getMessage());
        }
        catch (NoSuchAlgorithmException e)
        {
            THLog.d("KeyHash Error", e.getMessage());
        }
    }
}
