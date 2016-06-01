package com.ayondo.academy.fragments.social.friend;

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

    /**
     * This is used to filter in the adapter
     * @return
     */
    @Override public final String toString()
    {
        return name;
    }
}
