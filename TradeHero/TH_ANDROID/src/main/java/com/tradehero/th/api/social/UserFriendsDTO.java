package com.ayondo.academy.api.social;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.social.key.FriendKey;

abstract public class UserFriendsDTO
        implements Comparable<UserFriendsDTO>, DTO
{
    public String name;       // name
    public int thUserId;
    public boolean alreadyInvited; //has an invitation been sent already

    public String email; // Not provided from the server
    public boolean selected = false; // HACK not from server

    @NonNull abstract public FriendKey getFriendKey();

    @DrawableRes abstract public int getNetworkLabelImage();

    public String getProfilePictureURL()
    {
        return null;
    }

    abstract public InviteDTO createInvite();

    @NonNull abstract public String getAnalyticsTag();

    public boolean isTradeHeroUser()
    {
        return thUserId > 0;
    }

    public boolean isNonTradeHeroUser()
    {
        return !isTradeHeroUser();
    }

    @Override public int hashCode()
    {
        return name == null ? 0 : name.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        if (other == this)
        {
            return true;
        }
        return other instanceof UserFriendsDTO
                && equalFields((UserFriendsDTO) other);
    }

    protected boolean equalFields(@NonNull UserFriendsDTO other)
    {
        return (thUserId == other.thUserId) &&
            name == null ? other.name == null : name.equals(other.name);
    }

    @Override
    public int compareTo(@NonNull UserFriendsDTO another)
    {
        if (isTradeHeroUser())
        {
            if (!another.isTradeHeroUser())
            {
                return -1;
            }
            else
            {
                return name.compareToIgnoreCase(another.name);
            }
        }
        else
        {
            if (another.isTradeHeroUser())
            {
                return 1;
            }
            else
            {
                return name.compareToIgnoreCase(another.name);
            }
        }
    }
}
