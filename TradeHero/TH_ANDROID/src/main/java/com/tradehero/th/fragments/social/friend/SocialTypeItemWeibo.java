package com.tradehero.th.fragments.social.friend;

import com.tradehero.thm.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class SocialTypeItemWeibo extends SocialTypeItem
{
    public SocialTypeItemWeibo()
    {
        super(R.drawable.icn_weibo_round, R.string.invite_from_weibo, R.drawable.social_item_weibo, SocialNetworkEnum.WB);
    }
}
