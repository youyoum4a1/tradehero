package com.androidth.general.fragments.social.friend;

import com.androidth.general.api.social.UserFriendsDTO;

class SocialFriendListItemUserDTO implements SocialFriendListItemDTO
{
    public UserFriendsDTO userFriendsDTO;
    public boolean isSelected;

    //<editor-fold desc="Constructors">
    SocialFriendListItemUserDTO(UserFriendsDTO userFriendsDTO)
    {
        this.userFriendsDTO = userFriendsDTO;
    }
    //</editor-fold>

    /**
     * This is used to filter in the adapter
     * @return
     */
    @Override public final String toString()
    {
        return userFriendsDTO.name;
    }
}
