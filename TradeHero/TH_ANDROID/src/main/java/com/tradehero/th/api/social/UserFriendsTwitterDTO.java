package com.tradehero.th.api.social;

import com.tradehero.th.R;
import org.jetbrains.annotations.NotNull;

public class UserFriendsTwitterDTO extends UserFriendsDTO
{
    public static final String TWITTER_ID = "twId";

    @NotNull public String twId;
    public String twPicUrl;

    //<editor-fold desc="Constructors">
    public UserFriendsTwitterDTO()
    {
        super();
    }
    //</editor-fold>

    @Override public int getNetworkLabelImage()
    {
        return R.drawable.icon_share_tw_on;
    }

    @Override public String getProfilePictureURL()
    {
        return twPicUrl;
    }

    @Override public InviteDTO createInvite()
    {
        return new InviteTwitterDTO(twId);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ twId.hashCode();
    }

    @Override protected boolean equals(@NotNull UserFriendsDTO other)
    {
        return super.equals(other) &&
                other instanceof UserFriendsTwitterDTO &&
                twId.equals(((UserFriendsTwitterDTO) other).twId);
    }
}
