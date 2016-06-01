package com.ayondo.academy.models.share;

import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.SocialNetworkEnum;

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

    @Override @NonNull public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.WB;
    }
}
