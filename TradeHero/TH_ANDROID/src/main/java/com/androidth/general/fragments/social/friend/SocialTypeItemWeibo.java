package com.androidth.general.fragments.social.friend;

import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;

public class SocialTypeItemWeibo extends SocialTypeItem
{
    public SocialTypeItemWeibo()
    {
        super(R.drawable.icn_wb_white, R.string.invite_from_weibo, R.drawable.weibo_selector, SocialNetworkEnum.WB);
    }
}
