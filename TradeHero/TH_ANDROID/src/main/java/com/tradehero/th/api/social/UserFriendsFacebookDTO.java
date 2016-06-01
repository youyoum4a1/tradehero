package com.ayondo.academy.api.social;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.key.FacebookFriendKey;
import com.ayondo.academy.utils.metrics.AnalyticsConstants;

public class UserFriendsFacebookDTO extends UserFriendsDTO
{
    public static final String FACEBOOK_ID = "fbId";

    @NonNull public String fbId;       // FB id
    public String fbPicUrl;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") // Needed for deserialisation
    UserFriendsFacebookDTO()
    {
        super();
    }

    public UserFriendsFacebookDTO(@NonNull String fbId)
    {
        this.fbId = fbId;
    }

    public UserFriendsFacebookDTO(@NonNull String fbId, String fbPicUrl, String name)
    {
        this.name = name;
        this.fbId = fbId;
        this.fbPicUrl = fbPicUrl;
    }
    //</editor-fold>

    @NonNull @Override public FacebookFriendKey getFriendKey()
    {
        return new FacebookFriendKey(fbId);
    }

    @Override @DrawableRes public int getNetworkLabelImage()
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

    @NonNull @Override public String getAnalyticsTag()
    {
        return AnalyticsConstants.Facebook;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ fbId.hashCode();
    }

    @Override protected boolean equalFields(@NonNull UserFriendsDTO other)
    {
        return super.equalFields(other) &&
                other instanceof UserFriendsFacebookDTO &&
                fbId.equals(((UserFriendsFacebookDTO) other).fbId);
    }
}
