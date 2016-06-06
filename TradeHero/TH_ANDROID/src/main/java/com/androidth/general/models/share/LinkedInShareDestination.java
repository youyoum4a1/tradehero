package com.androidth.general.models.share;

import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;

public class LinkedInShareDestination extends BaseShareDestination
    implements ShareDestinationWithEnum
{
    @Override public int getNameResId()
    {
        return R.string.linkedin;
    }

    @Override public int getIdResId()
    {
        return R.integer.social_destination_id_linkedin;
    }

    @Override @NonNull public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.LN;
    }
}
