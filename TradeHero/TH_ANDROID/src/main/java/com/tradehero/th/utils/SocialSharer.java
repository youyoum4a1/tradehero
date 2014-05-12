package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.wxapi.WeChatDTO;


public interface SocialSharer
{
    void share(Context context, WeChatDTO weChatDTO);
}
