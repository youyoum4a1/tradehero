package com.tradehero.th.api.social;

import com.tradehero.th.R;
import android.support.annotation.NonNull;

public class UserFriendsTwitterDTO extends UserFriendsDTO
{
    public static final String TWITTER_ID = "twId";

    @NonNull public String twId;
    public String twPicUrl;

    //<editor-fold desc="Constructors">
    public UserFriendsTwitterDTO()
    {
        super();
    }

    public UserFriendsTwitterDTO(@NonNull String twId)
    {
        this.twId = twId;
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

    @Override protected boolean equals(@NonNull UserFriendsDTO other)
    {
        return super.equals(other) &&
                other instanceof UserFriendsTwitterDTO &&
                twId.equals(((UserFriendsTwitterDTO) other).twId);
    }
}
