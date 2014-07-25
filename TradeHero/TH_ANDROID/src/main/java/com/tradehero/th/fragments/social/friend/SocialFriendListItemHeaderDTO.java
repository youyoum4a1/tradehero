package com.tradehero.th.fragments.social.friend;

class SocialFriendListItemHeaderDTO implements SocialFriendListItemDTO
{
    public String header;
    public String name;

    //<editor-fold desc="Constructors">
    SocialFriendListItemHeaderDTO(String header)
    {
        this.header = header;
        name = "";
    }
    //</editor-fold>
}
