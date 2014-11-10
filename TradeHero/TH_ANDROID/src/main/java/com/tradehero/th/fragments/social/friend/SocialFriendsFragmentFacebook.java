package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;

public class SocialFriendsFragmentFacebook extends SocialFriendsFragment
{
    @Inject Provider<SocialFriendHandlerFacebook> facebookSocialFriendHandlerProvider;

    @Override
    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.FB;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.invite_social_friend, getString(R.string.facebook));
    }

    @Override
    protected boolean canInviteAll()
    {
        return true;
    }

    @Override
    protected void createFriendHandler()
    {
        if (socialFriendHandler == null)
        {
            socialFriendHandler = facebookSocialFriendHandlerProvider.get();
        }
    }

    @Override
    protected void handleInviteUsers(List<UserFriendsDTO> usersToInvite)
    {
        // TODO
        super.handleInviteUsers(usersToInvite);
    }

    @Override
    protected RequestObserver<BaseResponseDTO> createInviteObserver(List<UserFriendsDTO> usersToInvite)
    {
        return new FacebookInviteFriendObserver(usersToInvite);
    }

    class FacebookInviteFriendObserver extends FacebookRequestObserver
    {
        final List<UserFriendsDTO> usersToInvite;

        //<editor-fold desc="Constructors">
        public FacebookInviteFriendObserver(@NonNull Context context, List<UserFriendsDTO> usersToInvite)
        {
            super(context);
            this.usersToInvite = usersToInvite;
        }

        private FacebookInviteFriendObserver(List<UserFriendsDTO> usersToInvite)
        {
            this(getActivity(), usersToInvite);
        }
        //</editor-fold>

        @Override
        public void success()
        {
            handleInviteSuccess(usersToInvite);
        }

        @Override
        public void failure()
        {
            handleInviteError();
        }
    }
}
