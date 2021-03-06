package com.tradehero.th.api.social;

import com.tradehero.th.R;
import android.support.annotation.NonNull;

public class UserFriendsLinkedinDTO extends UserFriendsDTO
{
    public static final String LINKEDIN_ID = "liId";

    @NonNull public String liId;       // or LI id
    public String liPicUrl;   // LI gives is pics (FB pics can be dynamically gen'd)
    public String liHeadline; // LI: gives current position/title?

    //<editor-fold desc="Constructors">
    public UserFriendsLinkedinDTO()
    {
        super();
    }

    public UserFriendsLinkedinDTO(@NonNull String liId)
    {
        this.liId = liId;
    }
    //</editor-fold>

    @Override public int getNetworkLabelImage()
    {
        return R.drawable.icon_share_linkedin_on;
    }

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

    @Override protected boolean equals(@NonNull UserFriendsDTO other)
    {
        return super.equals(other) &&
                other instanceof UserFriendsLinkedinDTO &&
                liId.equals(((UserFriendsLinkedinDTO) other).liId);
    }
}
