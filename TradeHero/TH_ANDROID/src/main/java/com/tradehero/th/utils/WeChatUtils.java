package com.tradehero.th.utils;

import android.content.Context;
import android.content.Intent;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.wxapi.WXEntryActivity;
import com.tradehero.th.wxapi.WXMessageType;
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

    @Override public void share(Context context, DTOKey shareDtoKey)
    {
        if (shareDtoKey instanceof DiscussionKey)
        {
            DiscussionKey discussionKey = (DiscussionKey) shareDtoKey;

            Intent intent = new Intent(context, WXEntryActivity.class);

            intent.putExtra(WXEntryActivity.WECHAT_MESSAGE_TYPE_KEY, WXMessageType.News.getType());
            intent.putExtra(WXEntryActivity.WECHAT_MESSAGE_ID_KEY, discussionKey.key);
            context.startActivity(intent);
        }
    }
}
