package com.tradehero.th.utils;

import android.content.Context;
import android.content.Intent;
import com.tradehero.th.wxapi.WXEntryActivity;
import com.tradehero.th.wxapi.WeChatDTO;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by alex on 14-4-8.
 */
@Singleton
public class WeChatUtils implements SocialSharer
{
    @Inject public WeChatUtils()
    {
    }

    @Override public void share(Context context, WeChatDTO weChatDTO)
    {
        Intent intent = new Intent(context, WXEntryActivity.class);
        intent.putExtra(WXEntryActivity.WECHAT_MESSAGE_TYPE_KEY, weChatDTO.type);
        intent.putExtra(WXEntryActivity.WECHAT_MESSAGE_ID_KEY, weChatDTO.id);
        if (weChatDTO.title != null)
        {
            intent.putExtra(WXEntryActivity.WECHAT_MESSAGE_TITLE_KEY, weChatDTO.title);
        }
        if (weChatDTO.imageURL != null && !weChatDTO.imageURL.isEmpty())
        {
            intent.putExtra(WXEntryActivity.WECHAT_MESSAGE_IMAGE_URL_KEY, weChatDTO.imageURL);
        }
        context.startActivity(intent);
    }
}
