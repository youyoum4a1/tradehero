package com.androidth.general.api.social;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.social.key.WeiboFriendKey;
import com.androidth.general.utils.metrics.AnalyticsConstants;

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

    @NonNull @Override public WeiboFriendKey getFriendKey()
    {
        return new WeiboFriendKey(wbId);
    }

    @Override @DrawableRes public int getNetworkLabelImage()
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

    @NonNull @Override public String getAnalyticsTag()
    {
        return AnalyticsConstants.WeiBo;
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
