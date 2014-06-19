package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeaderboardUserId implements Comparable, DTOKey
{
    public static final String BUNDLE_KEY_USER_ID = LeaderboardUserId.class.getName() + ".userId";
    public static final String BUNDLE_KEY_LBMUID = LeaderboardUserId.class.getName() + ".lbmuid";

    public final int userId;
    public final long lbmuid;

    //<editor-fold desc="Constructors">
    public LeaderboardUserId(int userId, long lbmuid)
    {
        this.userId = userId;
        this.lbmuid = lbmuid;
    }

    public LeaderboardUserId(Bundle args)
    {
        this.userId = args.getInt(BUNDLE_KEY_USER_ID);
        this.lbmuid = args.getLong(BUNDLE_KEY_LBMUID);
    }
    //</editor-fold>

    @JsonIgnore @NotNull
    public UserBaseKey createUserBaseKey()
    {
        return new UserBaseKey(userId);
    }

    @JsonIgnore @NotNull
    public LeaderboardMarkUserId createLeaderboardMarkUserId()
    {
        return new LeaderboardMarkUserId((int) lbmuid);
    }

    @Override public int hashCode()
    {
        return (Integer.valueOf(userId).hashCode() ^
                Long.valueOf(lbmuid).hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderboardUserId) && equals((LeaderboardUserId) other);
    }

    public boolean equals(LeaderboardUserId other)
    {
        return (other != null) &&
                (userId == other.userId &&
                (lbmuid == other.lbmuid));
    }

    @Override public int compareTo(@Nullable Object other)
    {
        if (other == null)
        {
            return 1;
        }

        if (other.getClass() == getClass())
        {
            return compareTo(getClass().cast(other));
        }
        return other.getClass().getName().compareTo(getClass().getName());
    }

    public int compareTo(LeaderboardUserId other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        int lbmuidComp = Long.valueOf(lbmuid).compareTo(other.lbmuid);
        if (lbmuidComp != 0)
        {
            return lbmuidComp;
        }

        return Integer.valueOf(userId).compareTo(other.userId);
    }

    @Override public String toString()
    {
        return String.format("[userId=%d; lbmuid=%d]", userId, lbmuid);
    }
}
