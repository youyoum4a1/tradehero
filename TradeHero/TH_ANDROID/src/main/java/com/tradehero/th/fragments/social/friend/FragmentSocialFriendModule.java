package com.tradehero.th.fragments.social.friend;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
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
