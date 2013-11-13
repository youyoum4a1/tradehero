package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOKey;

/**
 * Created by julien on 1/11/13
 */
public class OwnedLeaderboardPositionId implements Comparable, DTOKey, DTO
{
    public final static String BUNDLE_KEY_LEADERBOARD_MARK_USER_ID = OwnedLeaderboardPositionId.class.getName() + ".leaderboardMarkUserId";
    public final static String BUNDLE_KEY_LEADERBOARD_MARK_USER_POSITION_ID = OwnedLeaderboardPositionId.class.getName() + ".leaderboardMarkUserPositionId";

    public final Integer leaderboardMarkUserId;
    public final Integer leaderboardMarkUserPositionId;

    //<editor-fold desc="Constructors">
    public OwnedLeaderboardPositionId(Integer leaderboardMarkUserId, Integer leaderboardMarkUserPositionId)
    {
        this.leaderboardMarkUserId = leaderboardMarkUserId;
        this.leaderboardMarkUserPositionId = leaderboardMarkUserPositionId;
    }

    public OwnedLeaderboardPositionId(LeaderboardMarkUserId leaderboardMarkUserId, Integer leaderboardMarkUserPositionId)
    {
        this(leaderboardMarkUserId.key, leaderboardMarkUserPositionId);
    }

    public OwnedLeaderboardPositionId(Bundle args)
    {
        this.leaderboardMarkUserId = args.containsKey(BUNDLE_KEY_LEADERBOARD_MARK_USER_ID) ? args.getInt(BUNDLE_KEY_LEADERBOARD_MARK_USER_ID) : null;
        this.leaderboardMarkUserPositionId = args.containsKey(BUNDLE_KEY_LEADERBOARD_MARK_USER_POSITION_ID) ? args.getInt(
                BUNDLE_KEY_LEADERBOARD_MARK_USER_POSITION_ID) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return (this.leaderboardMarkUserId == null ? 0 : leaderboardMarkUserId.hashCode()) ^
                (leaderboardMarkUserPositionId == null ? 0 : leaderboardMarkUserPositionId.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof OwnedLeaderboardPositionId) && equals((OwnedLeaderboardPositionId) other);
    }

    public boolean equals(OwnedLeaderboardPositionId other)
    {
        return (other != null) &&
                (leaderboardMarkUserId == null ? other.leaderboardMarkUserId == null : leaderboardMarkUserId.equals(other.leaderboardMarkUserId)) &&
                (leaderboardMarkUserPositionId == null ? other.leaderboardMarkUserPositionId
                        == null : leaderboardMarkUserPositionId.equals(other.leaderboardMarkUserPositionId));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == OwnedLeaderboardPositionId.class)
        {
            return compareTo((OwnedLeaderboardPositionId) o);
        }
        return o.getClass().getName().compareTo(OwnedLeaderboardPositionId.class.getName());
    }

    public int compareTo(OwnedLeaderboardPositionId other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        int lbmuComp = leaderboardMarkUserId.compareTo(other.leaderboardMarkUserId);
        if (lbmuComp != 0)
        {
            return lbmuComp;
        }

        return leaderboardMarkUserPositionId.compareTo(other.leaderboardMarkUserPositionId);
    }

    public boolean isValid()
    {
        return leaderboardMarkUserId != null && leaderboardMarkUserPositionId != null;
    }

    public void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_LEADERBOARD_MARK_USER_ID, leaderboardMarkUserId);
        args.putInt(BUNDLE_KEY_LEADERBOARD_MARK_USER_POSITION_ID, leaderboardMarkUserPositionId);
    }

    public LeaderboardMarkUserId getLeaderboardMarkUserKey()
    {
        return new LeaderboardMarkUserId(leaderboardMarkUserId);
    }

    @Override public String toString()
    {
        return String.format("[leaderboardMarkUserId=%d; leaderboardMarkUserPositionId=%d]", leaderboardMarkUserId, leaderboardMarkUserPositionId);
    }

    public boolean isLocked()
    {
        return leaderboardMarkUserPositionId < 0;
    }
}
