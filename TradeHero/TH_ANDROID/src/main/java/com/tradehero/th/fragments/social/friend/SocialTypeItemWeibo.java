package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class SocialTypeItemWeibo extends SocialTypeItem
{
    public SocialTypeItemWeibo()
    {
        super(R.drawable.icn_wb_white, R.string.invite_from_weibo, R.drawable.weibo_selector, SocialNetworkEnum.WB);
    }
}
