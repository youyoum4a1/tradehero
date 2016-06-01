package com.ayondo.academy.api.social;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.key.LinkedinFriendKey;
import com.ayondo.academy.utils.metrics.AnalyticsConstants;

public class UserFriendsLinkedinDTO extends UserFriendsDTO
{
    public static final String LINKEDIN_ID = "liId";

    @NonNull public String liId;       // or LI id
    public String liPicUrl;   // LI gives is pics (FB pics can be dynamically gen'd)
    public String liHeadline; // LI: gives current position/title?

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") // Needed for deserialisation
    UserFriendsLinkedinDTO()
    {
        super();
    }

    public UserFriendsLinkedinDTO(@NonNull String liId)
    {
        this.liId = liId;
    }
    //</editor-fold>

    @NonNull @Override public LinkedinFriendKey getFriendKey()
    {
        return new LinkedinFriendKey(liId);
    }

    @Override @DrawableRes public int getNetworkLabelImage()
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

    @NonNull @Override public String getAnalyticsTag()
    {
        return AnalyticsConstants.Linkedin;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ liId.hashCode();
    }

    @Override protected boolean equalFields(@NonNull UserFriendsDTO other)
    {
        return super.equalFields(other) &&
                other instanceof UserFriendsLinkedinDTO &&
                liId.equals(((UserFriendsLinkedinDTO) other).liId);
    }
}
