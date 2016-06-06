package com.androidth.general.models.share;

import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;

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

    @Override @NonNull public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.TW;
    }
}
