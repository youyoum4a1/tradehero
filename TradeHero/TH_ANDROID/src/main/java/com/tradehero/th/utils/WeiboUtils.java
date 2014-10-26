package com.tradehero.th.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.tradehero.th.auth.weibo.WeiboAuthenticationProvider;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.wxapi.WXEntryActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WeiboUtils {

    private final WeiboAuthenticationProvider provider;

    @Inject
    public WeiboUtils(WeiboAuthenticationProvider provider)
    {
        this.provider = provider;
        THUser.registerAuthenticationProvider(provider);
    }

    public void logIn(Context context, LogInCallback callback)
    {
        provider.with(context);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }

    public void authorizeCallBack(int requestCode, int resultCode, Intent data){
        provider.authorizeCallBack(requestCode,resultCode,data);
    }

    public static String getShareContentWeibo(String outputStr, String downloadCNTradeHeroAPKURl){
        downloadCNTradeHeroAPKURl = "  " + downloadCNTradeHeroAPKURl;
        String shareStrs[] = WXEntryActivity.parseContent(outputStr);
        String content = shareStrs[0];
        String url = shareStrs[1];
        if(TextUtils.isEmpty(url)||!NetworkUtils.isCNTradeHeroURL(url)){
            int nowLimit = Constants.SHARE_WEIBO_CONTENT_LENGTH_LIMIT -downloadCNTradeHeroAPKURl.length();
            if (outputStr.length() > nowLimit) {
                outputStr = outputStr.substring(0, nowLimit) + downloadCNTradeHeroAPKURl;
            }else{
                outputStr = outputStr + downloadCNTradeHeroAPKURl;
            }
        }else{
            if (outputStr.length() > Constants.SHARE_WEIBO_CONTENT_LENGTH_LIMIT) {
                outputStr = outputStr.substring(0, Constants.SHARE_WEIBO_CONTENT_LENGTH_LIMIT);
            }
        }
        return outputStr;
    }

}
