package com.tradehero.th.network.share;

import android.content.Context;
import android.content.Intent;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.wxapi.WXEntryActivity;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import javax.inject.Inject;

public class SocialSharerImpl implements SocialSharer
{
    private final Context context;

    @Inject public SocialSharerImpl(Context context)
    {
        this.context = context;
    }

    @Override public void share(SocialShareFormDTO shareFormDTO)
    {
        if (shareFormDTO instanceof WeChatDTO)
        {
            share((WeChatDTO) shareFormDTO);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + shareFormDTO.getClass());
        }
    }

    public void share(WeChatDTO weChatDTO)
    {
        context.startActivity(createWeChatIntent(weChatDTO));
    }

    public Intent createWeChatIntent(WeChatDTO weChatDTO)
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
        return intent;
    }
}
