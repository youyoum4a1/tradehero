package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class SocialTypeItemTwitter extends SocialTypeItem
{
    public SocialTypeItemTwitter()
    {
        super(R.drawable.icn_tw_white, R.string.invite_from_twitter, R.drawable.twitter_selector, SocialNetworkEnum.TW);
    }
}
