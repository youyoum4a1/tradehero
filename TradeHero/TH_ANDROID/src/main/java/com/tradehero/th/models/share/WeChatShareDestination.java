package com.ayondo.academy.models.share;

import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.SocialNetworkEnum;

public class WeChatShareDestination extends BaseShareDestination
    implements ShareDestinationWithEnum
{
    @Override public int getNameResId()
    {
        return R.string.wechat;
    }

    @Override public int getIdResId()
    {
        return R.integer.social_destination_id_wechat;
    }

    @Override @NonNull public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.WECHAT;
    }
}
