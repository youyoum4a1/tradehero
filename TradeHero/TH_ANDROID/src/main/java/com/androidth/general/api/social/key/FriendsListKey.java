package com.androidth.general.api.social.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTOKey;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.users.UserBaseKey;

public class FriendsListKey implements DTOKey
{
    @NonNull public final UserBaseKey userBaseKey;
    @Nullable public final SocialNetworkEnum socialNetworkEnum;
    @Nullable public final String searchQuery;

    //<editor-fold desc="Constructors">
    public FriendsListKey(@NonNull UserBaseKey userBaseKey)
    {
        this(userBaseKey, null);
    }

    public FriendsListKey(
            @NonNull UserBaseKey userBaseKey,
            @Nullable SocialNetworkEnum socialNetworkEnum)
    {
        this(userBaseKey, socialNetworkEnum, null);
    }

    public FriendsListKey(
            @NonNull UserBaseKey userBaseKey,
            @Nullable SocialNetworkEnum socialNetworkEnum,
            @Nullable String searchQuery)
    {
        this.userBaseKey = userBaseKey;
        this.socialNetworkEnum = socialNetworkEnum;
        this.searchQuery = searchQuery;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return userBaseKey.hashCode() ^
                (socialNetworkEnum == null ? 0 : socialNetworkEnum.hashCode()) ^
                (searchQuery == null ? 0 : searchQuery.hashCode());
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return other instanceof FriendsListKey
                && equalFields((FriendsListKey) other);
    }

    protected boolean equalFields(@NonNull FriendsListKey other)
    {
        return userBaseKey.equals(other.userBaseKey) &&
                (socialNetworkEnum == null ? other.socialNetworkEnum == null : socialNetworkEnum.equals(other.socialNetworkEnum)) &&
                (searchQuery == null ? other.searchQuery == null : searchQuery.equals(other.searchQuery));
    }
}
