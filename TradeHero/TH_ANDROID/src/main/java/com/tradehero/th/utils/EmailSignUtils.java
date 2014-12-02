package com.tradehero.th.utils;

import android.text.TextUtils;
import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.THUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSignUtils
{
    private static EmailAuthenticationProvider provider;
    private static boolean isInitialized = false;

    public static void initialize()
    {
        provider = new EmailAuthenticationProvider();
        THUser.registerAuthenticationProvider(provider);
        isInitialized = true;
    }


    public static boolean isValidEmail(CharSequence charSequence) {
        if(charSequence==null){
            return false;
        }
        if(TextUtils.isEmpty(charSequence.toString())){
            return false;
        }
        boolean isValid = false;
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(charSequence);
        if (m.matches()) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }
}
