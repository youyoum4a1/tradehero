package com.androidth.general.fragments.social.friend;

import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;

public class SocialTypeItemWechat extends SocialTypeItem
{
    public SocialTypeItemWechat()
    {
        super(R.drawable.icn_wc_white, R.string.invite_from_wechat, R.drawable.wechat_selector, SocialNetworkEnum.WECHAT);
    }
}
