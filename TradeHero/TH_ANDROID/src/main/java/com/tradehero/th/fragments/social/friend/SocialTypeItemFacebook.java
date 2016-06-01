package com.ayondo.academy.fragments.social.friend;

import com.ayondo.academy.R;
import com.ayondo.academy.api.social.SocialNetworkEnum;

public class SocialTypeItemFacebook extends SocialTypeItem
{
    public SocialTypeItemFacebook()
    {
        super(R.drawable.icn_fb_white, R.string.invite_from_facebook, R.drawable.facebook_selector, SocialNetworkEnum.FB);
    }
}
