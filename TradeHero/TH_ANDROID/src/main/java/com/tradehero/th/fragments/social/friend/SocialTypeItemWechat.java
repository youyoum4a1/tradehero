package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class SocialTypeItemWechat extends SocialTypeItem
{
    public SocialTypeItemWechat()
    {
        super(R.drawable.icn_wechat_round, R.string.invite_from_wechat, R.drawable.social_item_wechat, SocialNetworkEnum.WECHAT);
    }
}
