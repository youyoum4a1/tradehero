package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

/**
 * Created by wangliang on 14-5-26.
 */
public class LinkedInSocialFriendsFragment extends SocialFriendsFragment {

    @Override
    protected SocialNetworkEnum getSocialNetwork() {
        return SocialNetworkEnum.LN;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.invite_social_friend,getString(R.string.linkedin));
    }
}
