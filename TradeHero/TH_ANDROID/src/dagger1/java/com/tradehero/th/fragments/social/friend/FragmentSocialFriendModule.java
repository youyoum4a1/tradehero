package com.ayondo.academy.fragments.social.friend;

import dagger.Module;

@Module(
        injects = {
                InviteCodeViewLinear.class,
                InviteCodeDialogFragment.class,
                FriendsInvitationFragment.class,
                SocialFriendsFragmentFacebook.class,
                SocialFriendsFragmentTwitter.class,
                SocialFriendsFragmentLinkedIn.class,
                SocialFriendsFragmentWeibo.class,
                SocialFriendUserView.class,
        },
        library = true,
        complete = false
)
public class FragmentSocialFriendModule
{
}
