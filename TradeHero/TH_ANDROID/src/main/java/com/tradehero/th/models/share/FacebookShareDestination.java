package com.ayondo.academy.models.share;

import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.SocialNetworkEnum;

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

    @Override @NonNull public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.FB;
    }
}
