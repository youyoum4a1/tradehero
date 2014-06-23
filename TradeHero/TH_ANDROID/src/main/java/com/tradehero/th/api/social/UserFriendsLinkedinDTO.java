package com.tradehero.th.api.social;

import org.jetbrains.annotations.NotNull;

public class UserFriendsLinkedinDTO extends UserFriendsDTO
{
    public static final String LINKEDIN_ID = "liId";

    @NotNull public String liId;       // or LI id
    public String liPicUrl;   // LI gives is pics (FB pics can be dynamically gen'd)
    public String liHeadline; // LI: gives current position/title?

    //<editor-fold desc="Constructors">
    public UserFriendsLinkedinDTO()
    {
        super();
    }
    //</editor-fold>

    @Override public String getProfilePictureURL()
    {
        return liPicUrl;
    }

    @Override public InviteDTO createInvite()
    {
        return new InviteLinkedinDTO(liId);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ liId.hashCode();
    }

    @Override protected boolean equals(@NotNull UserFriendsDTO other)
    {
        return super.equals(other) &&
                other instanceof UserFriendsLinkedinDTO &&
                liId.equals(((UserFriendsLinkedinDTO) other).liId);
    }
}
