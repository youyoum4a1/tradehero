package com.tradehero.th.models.share;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class FacebookShareDestination extends BaseShareDestination
    implements ShareDestinationWithEnum
{
    @Override public int getNameResId()
    {
        return R.string.facebook;
    }

    @Override public int getIdResId()
    {
        return R.integer.social_destination_id_facebook;
    }

    @Override public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.FB;
    }
}
