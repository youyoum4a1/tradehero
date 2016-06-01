package com.ayondo.academy.fragments.social.friend;

import com.ayondo.academy.R;
import com.ayondo.academy.api.social.SocialNetworkEnum;

public class SocialTypeItemWechat extends SocialTypeItem
{
    public SocialTypeItemWechat()
    {
        super(R.drawable.icn_wc_white, R.string.invite_from_wechat, R.drawable.wechat_selector, SocialNetworkEnum.WECHAT);
    }
}
