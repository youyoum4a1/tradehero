package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOKey;

/**
 * Created by julien on 1/11/13
 */
public class OwnedLbPositionId implements Comparable, DTOKey, DTO
{
    public final static String BUNDLE_KEY_LBMUP_ID = OwnedLbPositionId.class.getName() + ".lbmupId";
    public final static String BUNDLE_KEY_LBMU_ID = OwnedLbPositionId.class.getName() + ".lbmuId";

    public final Integer lbmuId;
    public final Integer lbmupId;

    public OwnedLbPositionId(Integer lbmuId, Integer lbmupId)
    {
        this.lbmuId = lbmuId;
        this.lbmupId = lbmupId;
    }

    public OwnedLbPositionId(LbUserId lbmuId, Integer lbmupId)
    {
        this(lbmuId.key, lbmupId);
    }


    public OwnedLbPositionId(Bundle args)
    {
        this.lbmuId= args.containsKey(BUNDLE_KEY_LBMU_ID) ? args.getInt(BUNDLE_KEY_LBMU_ID) : null;
        this.lbmupId= args.containsKey(BUNDLE_KEY_LBMUP_ID) ? args.getInt(BUNDLE_KEY_LBMUP_ID) : null;
    }

    @Override public int hashCode()
    {
        return (this.lbmuId == null ? 0 : lbmuId.hashCode()) ^
                (lbmupId == null ? 0 : lbmupId .hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof OwnedLbPositionId) && equals((OwnedLbPositionId) other);
    }

    public boolean equals(OwnedLbPositionId other)
    {
        return (other != null) &&
                (lbmuId == null ? other.lbmuId == null : lbmuId.equals(other.lbmuId)) &&
                (lbmupId == null ? other.lbmupId == null : lbmupId.equals(other.lbmupId));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == OwnedLbPositionId.class)
        {
            return compareTo((OwnedLbPositionId) o);
        }
        return o.getClass().getName().compareTo(OwnedLbPositionId.class.getName());
    }

    public int compareTo(OwnedLbPositionId other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        int lbmuComp = lbmuId.compareTo(other.lbmuId);
        if (lbmuComp != 0)
        {
            return lbmuComp;
        }

        return lbmupId.compareTo(other.lbmupId);
    }

    public boolean isValid()
    {
        return lbmuId != null && lbmupId!= null;
    }

    public void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_LBMU_ID, lbmuId);
        args.putInt(BUNDLE_KEY_LBMUP_ID, lbmupId);
    }

    public LbUserId getLeaderboardMarkUserKey()
    {
        return new LbUserId(lbmuId);
    }

    @Override public String toString()
    {
        return String.format("[lbmuId=%d; lbmupId=%d]", lbmuId, lbmupId);
    }
}
