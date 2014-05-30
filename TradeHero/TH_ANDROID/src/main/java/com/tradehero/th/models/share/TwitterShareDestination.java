package com.tradehero.th.models.share;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class TwitterShareDestination extends BaseShareDestination
    implements ShareDestinationWithEnum
{
    @Override public int getNameResId()
    {
        return R.string.twitter;
    }

    @Override public int getIdResId()
    {
        return R.integer.social_destination_id_twitter;
    }

    @Override public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.TW;
    }
}
