package com.tradehero.th.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.tradehero.common.utils.THLog;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import timber.log.Timber;

public final class NetworkUtils
{
    public static boolean isConnected(Context con)
    {
        ConnectivityManager connectivityManager;
        boolean connected = false;
        try
        {
            connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;
        }
        catch (Exception e)
        {
            Timber.d(e.getMessage());
        }
        return connected;
    }

    public static boolean isWiFiConnected(Context context)
    {
        boolean haveConnectedWifi = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (ni.isConnected())
                {
                    haveConnectedWifi = true;
                }
            }
        }
        return haveConnectedWifi;
    }

    public static boolean isMobileNetworkConnected(Context context)
    {
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
            {
                if (ni.isConnected())
                {
                    haveConnectedMobile = true;
                }
            }
        }
        return haveConnectedMobile;
    }

    public static SSLSocketFactory createBadSslSocketFactory()
    {
        try
        {
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager permissive = new X509TrustManager()
            {
                @Override public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException
                {
                }

                @Override public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException
                {
                }

                @Override public X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
            };
            context.init(null, new TrustManager[] {permissive}, new SecureRandom());
            return context.getSocketFactory();
        }
        catch (Exception e)
        {
            throw new AssertionError(e);
        }
    }

    public static boolean isCNTradeHeroURL(String str) {
        if(!str.contains("cn.tradehero.mobi")){
            return false;
        }
        //转换为小写
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();

    }
}
