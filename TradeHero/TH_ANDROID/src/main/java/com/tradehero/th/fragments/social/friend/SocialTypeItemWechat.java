package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class SocialTypeItemWechat extends SocialTypeItem
{
    public SocialTypeItemWechat()
    {
        super(R.drawable.icn_wc_white, R.string.invite_from_wechat, R.drawable.wechat_selector, SocialNetworkEnum.WECHAT);
    }
}
