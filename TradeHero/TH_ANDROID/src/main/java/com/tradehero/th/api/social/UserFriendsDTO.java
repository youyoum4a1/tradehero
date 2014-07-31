package com.tradehero.th.api.social;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class UserFriendsDTO
        implements Comparable<UserFriendsDTO>
{
    public String name;       // name
    public int thUserId;
    public boolean alreadyInvited; //has an invitation been sent already

    public String email; // Not provided from the server
    public boolean selected = false; // HACK not from server

    //<editor-fold desc="Constructors">
    public UserFriendsDTO()
    {
        super();
    }
    //</editor-fold>

    abstract public int getNetworkLabelImage();

    public String getProfilePictureURL()
    {
        return null;
    }

    abstract public InviteDTO createInvite();

    public boolean isTradeHeroUser()
    {
        return thUserId > 0;
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
        if (other == null || other.getClass() != getClass())
        {
            return false;
        }
        return equals((UserFriendsDTO) other);
    }

    protected boolean equals(@NotNull UserFriendsDTO other)
    {
        return (thUserId == other.thUserId) &&
            name == null ? other.name == null : name.equals(other.name);
    }

    @Override
    public int compareTo(@NotNull UserFriendsDTO another)
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
