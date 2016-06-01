package com.ayondo.academy.fragments.social.friend;

import com.ayondo.academy.R;
import com.ayondo.academy.api.social.SocialNetworkEnum;

public class SocialTypeItemTwitter extends SocialTypeItem
{
    public SocialTypeItemTwitter()
    {
        super(R.drawable.icn_tw_white, R.string.invite_from_twitter, R.drawable.twitter_selector, SocialNetworkEnum.TW);
    }
}
