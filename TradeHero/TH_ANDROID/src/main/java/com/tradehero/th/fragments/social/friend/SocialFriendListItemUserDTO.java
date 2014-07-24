package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.UserFriendsDTO;

class SocialFriendListItemUserDTO implements SocialFriendListItemDTO
{
    public UserFriendsDTO userFriendsDTO;
    public boolean isChecked;

    //<editor-fold desc="Constructors">
    SocialFriendListItemUserDTO(UserFriendsDTO userFriendsDTO)
    {
        this.userFriendsDTO = userFriendsDTO;
    }
    //</editor-fold>
}
