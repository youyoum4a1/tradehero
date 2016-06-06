package com.androidth.general.fragments.social.friend;

import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;

public class SocialTypeItemTwitter extends SocialTypeItem
{
    public SocialTypeItemTwitter()
    {
        super(R.drawable.icn_tw_white, R.string.invite_from_twitter, R.drawable.twitter_selector, SocialNetworkEnum.TW);
    }
}
