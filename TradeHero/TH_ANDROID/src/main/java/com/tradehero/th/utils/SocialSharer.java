package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.wxapi.WeChatDTO;

/**
 * Created by alex on 14-4-8.
 */
public interface SocialSharer
{
    void share(Context context, WeChatDTO weChatDTO);
}
