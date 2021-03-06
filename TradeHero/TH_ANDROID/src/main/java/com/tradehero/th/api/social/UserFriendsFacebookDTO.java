package com.tradehero.th.api.social;

import com.tradehero.th.R;
import android.support.annotation.NonNull;

public class UserFriendsFacebookDTO extends UserFriendsDTO
{
    public static final String FACEBOOK_ID = "fbId";

    @NonNull public String fbId;       // FB id
    public String fbPicUrl;

    //<editor-fold desc="Constructors">
    public UserFriendsFacebookDTO()
    {
        super();
    }

    public UserFriendsFacebookDTO(@NonNull String fbId)
    {
        this.fbId = fbId;
    }
    //</editor-fold>

    @Override public int getNetworkLabelImage()
    {
        return R.drawable.icon_share_fb_on;
    }

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

    @Override protected boolean equals(@NonNull UserFriendsDTO other)
    {
        return super.equals(other) &&
                other instanceof UserFriendsFacebookDTO &&
                fbId.equals(((UserFriendsFacebookDTO) other).fbId);
    }
}
