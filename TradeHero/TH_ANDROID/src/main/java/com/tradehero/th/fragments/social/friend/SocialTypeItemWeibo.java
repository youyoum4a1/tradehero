package com.ayondo.academy.fragments.social.friend;

import com.ayondo.academy.R;
import com.ayondo.academy.api.social.SocialNetworkEnum;

public class SocialTypeItemWeibo extends SocialTypeItem
{
    public SocialTypeItemWeibo()
    {
        super(R.drawable.icn_wb_white, R.string.invite_from_weibo, R.drawable.weibo_selector, SocialNetworkEnum.WB);
    }
}
