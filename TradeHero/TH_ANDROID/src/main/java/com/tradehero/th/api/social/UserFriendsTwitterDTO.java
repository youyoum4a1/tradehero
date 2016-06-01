package com.ayondo.academy.api.social;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.key.TwitterFriendKey;
import com.ayondo.academy.utils.metrics.AnalyticsConstants;

public class UserFriendsTwitterDTO extends UserFriendsDTO
{
    public static final String TWITTER_ID = "twId";

    @NonNull public String twId;
    public String twPicUrl;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") // Needed for deserialisation
    UserFriendsTwitterDTO()
    {
        super();
    }

    public UserFriendsTwitterDTO(@NonNull String twId)
    {
        this.twId = twId;
    }
    //</editor-fold>

    @NonNull @Override public TwitterFriendKey getFriendKey()
    {
        return new TwitterFriendKey(twId);
    }

    @Override @DrawableRes public int getNetworkLabelImage()
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

    @NonNull @Override public String getAnalyticsTag()
    {
        return AnalyticsConstants.Twitter;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ twId.hashCode();
    }

    @Override protected boolean equalFields(@NonNull UserFriendsDTO other)
    {
        return super.equalFields(other) &&
                other instanceof UserFriendsTwitterDTO &&
                twId.equals(((UserFriendsTwitterDTO) other).twId);
    }
}
