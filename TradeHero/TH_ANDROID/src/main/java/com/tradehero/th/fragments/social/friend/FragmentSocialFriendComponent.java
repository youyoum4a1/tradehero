package com.tradehero.th.fragments.social.friend;

import dagger.Component;

/**
 * Created by tho on 9/9/2014.
 */
@Component
public interface FragmentSocialFriendComponent
{
    void injectInviteCodeViewLinear(InviteCodeViewLinear target);
    void injectInviteCodeDialogFragment(InviteCodeDialogFragment target);
    void injectFriendsInvitationFragment(FriendsInvitationFragment target);
    void injectSocialFriendsFragmentFacebook(SocialFriendsFragmentFacebook target);
    void injectSocialFriendsFragmentTwitter(SocialFriendsFragmentTwitter target);
    void injectSocialFriendsFragmentLinkedIn(SocialFriendsFragmentLinkedIn target);
    void injectSocialFriendsFragmentWeibo(SocialFriendsFragmentWeibo target);
    void injectSocialFriendUserView(SocialFriendUserView target);
}
