package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;

import static com.tradehero.th.fragments.social.friend.FacebookSocialFriendHandler.*;

/**
 * Created by wangliang on 14-5-26.
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

    @Override
    protected RequestCallback createInviteCallback(List<UserFriendsDTO> usersToInvite) {
        return new FacebookInviteFriendCallback(usersToInvite);
    }


    class FacebookInviteFriendCallback extends FacebookSocialFriendHandler.FacebookRequestCallback
    {

        List<UserFriendsDTO> usersToInvite;

        public FacebookInviteFriendCallback(Context context,List<UserFriendsDTO> usersToInvite) {
            super(context);
            this.usersToInvite = usersToInvite;
        }

        private FacebookInviteFriendCallback(List<UserFriendsDTO> usersToInvite)
        {
            super(getActivity());
            this.usersToInvite = usersToInvite;
        }

        @Override
        public void success() {
            handleInviteSuccess(usersToInvite);
        }

        @Override
        public void failure() {
           handleInviteError();
        }


    }
}
