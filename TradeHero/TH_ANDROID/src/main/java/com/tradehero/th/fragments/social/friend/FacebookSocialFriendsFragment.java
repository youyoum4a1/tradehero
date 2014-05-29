package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;

import java.util.List;

/**
 * Created by tradehero on 14-5-26.
 */
public class FacebookSocialFriendsFragment extends SocialFriendsFragment {

    @Override
    protected SocialNetworkEnum getSocialNetwork() {
        return SocialNetworkEnum.FB;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.invite_social_friend,getString(R.string.facebook));
    }

    @Override
    protected boolean canInviteAll() {
        return false;
    }

    @Override
    protected void createFriendHandler() {
        if (socialFriendHandler == null)
        {
            socialFriendHandler = new FacebookSocialFriendHandler(getActivity());
        }
    }

    @Override
    protected void handleInviteUsers(List<UserFriendsDTO> usersToInvite) {
        // TODO
        super.handleInviteUsers(usersToInvite);
    }

}
