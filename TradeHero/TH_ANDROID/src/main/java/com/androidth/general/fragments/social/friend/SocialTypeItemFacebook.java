package com.androidth.general.fragments.social.friend;

import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;

public class SocialTypeItemFacebook extends SocialTypeItem
{
    public SocialTypeItemFacebook()
    {
        super(R.drawable.icn_fb_white, R.string.invite_from_facebook, R.drawable.facebook_selector, SocialNetworkEnum.FB);
    }
}
