package com.tradehero.chinabuild.fragment.web;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.base.Application;
import com.tradehero.th.wxapi.WXEntryActivity;

/**
 * Created by palmer on 15/3/24.
 */
public class CallNativeFromJS {

    public CallNativeFromJS(){
    }

    @JavascriptInterface
    public void shareToWeChat(String url){
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.Advertisement;
        weChatDTO.title = url;

        Intent gotoShareToWeChatIntent = new Intent(Application.context(), WXEntryActivity.class);
        gotoShareToWeChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WXEntryActivity.putWeChatDTO(gotoShareToWeChatIntent, weChatDTO);
        Application.context().startActivity(gotoShareToWeChatIntent);
    }

}
