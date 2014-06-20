package com.tradehero.th.api.social;

import org.jetbrains.annotations.NotNull;

public class UserFriendsFacebookDTO extends UserFriendsDTO
{
    public static final String FACEBOOK_ID = "fbId";

    @NotNull public String fbId;       // FB id
    public String fbPicUrl;

    //<editor-fold desc="Constructors">
    public UserFriendsFacebookDTO()
    {
        super();
    }
    //</editor-fold>

    @Override public String getProfilePictureURL()
    {
        return fbPicUrl;
    }

    @Override public InviteDTO createInvite()
    {
        return new InviteFacebookDTO(fbId);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ fbId.hashCode();
    }

    @Override protected boolean equals(@NotNull UserFriendsDTO other)
    {
        return super.equals(other) &&
                other instanceof UserFriendsFacebookDTO &&
                fbId.equals(((UserFriendsFacebookDTO) other).fbId);
    }
}
