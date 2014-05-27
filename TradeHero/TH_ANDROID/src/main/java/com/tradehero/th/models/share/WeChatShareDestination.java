package com.tradehero.th.models.share;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class WeChatShareDestination extends BaseShareDestination
    implements ShareDestinationWithEnum
{
    @Override public int getNameResId()
    {
        return R.string.wechat;
    }

    @Override public int getIdResId()
    {
        return R.integer.share_destination_id_wechat;
    }

    @Override public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.WECHAT;
    }
}
