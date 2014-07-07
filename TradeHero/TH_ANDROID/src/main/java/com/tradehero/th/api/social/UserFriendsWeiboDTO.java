package com.tradehero.th.api.social;

import com.tradehero.th.R;
import org.jetbrains.annotations.NotNull;

public class UserFriendsWeiboDTO extends UserFriendsDTO
{
    public static final String WEIBO_ID = "wbId";

    @NotNull public String wbId;
    public String wbPicUrl;

    //<editor-fold desc="Constructors">
    public UserFriendsWeiboDTO()
    {
        super();
    }
    //</editor-fold>

    @Override public int getNetworkLabelImage()
    {
        return R.drawable.icn_weibo_round;
    }

    @Override public String getProfilePictureURL()
    {
        return wbPicUrl;
    }

    @Override public InviteDTO createInvite()
    {
        return new InviteWeiboDTO(wbId);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ wbId.hashCode();
    }

    @Override protected boolean equals(@NotNull UserFriendsDTO other)
    {
        return super.equals(other) &&
                other instanceof UserFriendsWeiboDTO &&
                wbId.equals(((UserFriendsWeiboDTO) other).wbId);
    }
}
