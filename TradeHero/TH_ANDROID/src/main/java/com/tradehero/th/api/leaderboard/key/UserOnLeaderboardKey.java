package com.tradehero.th.api.leaderboard.key;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserOnLeaderboardKey implements DTOKey
{
    @NotNull public final LeaderboardKey leaderboardKey;
    @NotNull public final UserBaseKey userBaseKey;

    public UserOnLeaderboardKey(
            @NotNull LeaderboardKey leaderboardKey,
            @NotNull UserBaseKey userBaseKey)
    {
        this.leaderboardKey = leaderboardKey;
        this.userBaseKey = userBaseKey;
    }

    @Override public int hashCode()
    {
        return leaderboardKey.hashCode() ^ userBaseKey.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(@Nullable Object other)
    {
        if (other == this)
        {
            return true;
        }
        return other != null
                && equalClass(other)
                && equalFields((UserOnLeaderboardKey) other);
    }

    protected boolean equalClass(@NotNull Object other)
    {
        return other.getClass().equals(getClass());
    }

    protected boolean equalFields(@NotNull UserOnLeaderboardKey other)
    {
        return leaderboardKey.equals(other.leaderboardKey)
                && userBaseKey.equals(other.userBaseKey);
    }
}
