package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

/**
 * Created by wangliang on 14-5-26.
 */
public class WeiboSocialFriendsFragment extends SocialFriendsFragment {

    @Override
    protected SocialNetworkEnum getSocialNetwork() {
        return SocialNetworkEnum.WB;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.invite_social_friend,getString(R.string.sina_weibo));
    }

    @Override
    protected boolean canInviteAll() {
        return false;
    }

    @Override
    protected boolean canInvite() {
        return false;
    }
}
