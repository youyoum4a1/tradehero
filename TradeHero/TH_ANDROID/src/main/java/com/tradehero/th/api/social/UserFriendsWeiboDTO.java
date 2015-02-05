package com.tradehero.th.api.social;

import android.support.annotation.NonNull;
import com.tradehero.th.R;

public class UserFriendsWeiboDTO extends UserFriendsDTO
{
    public static final String WEIBO_ID = "wbId";

    @NonNull public String wbId;
    public String wbPicUrl;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") // Needed for deserialisation
    UserFriendsWeiboDTO()
    {
        super();
    }

    public UserFriendsWeiboDTO(@NonNull String wbId)
    {
        this.wbId = wbId;
    }
    //</editor-fold>

    @Override public int getNetworkLabelImage()
    {
        return R.drawable.icn_wb_white;
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

    @Override protected boolean equalFields(@NonNull UserFriendsDTO other)
    {
        return super.equalFields(other) &&
                other instanceof UserFriendsWeiboDTO &&
                wbId.equals(((UserFriendsWeiboDTO) other).wbId);
    }
}
