package com.tradehero.th.models.share;

import com.tradehero.thm.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class WeiboShareDestination extends BaseShareDestination
    implements ShareDestinationWithEnum
{
    @Override public int getNameResId()
    {
        return R.string.sina_weibo;
    }

    @Override public int getIdResId()
    {
        return R.integer.social_destination_id_weibo;
    }

    @Override public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.WB;
    }
}
