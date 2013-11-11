package com.tradehero.th.api.social;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/1/13 Time: 12:29 PM To change this template use File | Settings | File Templates. */
public class FollowerId implements Comparable, DTOKey
{
    public final static String BUNDLE_KEY_FOLLOWED_ID = FollowerId.class.getName() + ".followedId";
    public final static String BUNDLE_KEY_FOLLOWER_ID = FollowerId.class.getName() + ".followerId";

    public final Integer followedId;
    public final Integer followerId;

    //<editor-fold desc="Constructors">
    public FollowerId(final Integer followedId, final Integer followerId)
    {
        this.followedId = followedId;
        this.followerId = followerId;
    }

    public FollowerId(Bundle args)
    {
        this.followedId = args.containsKey(BUNDLE_KEY_FOLLOWED_ID) ? args.getInt(BUNDLE_KEY_FOLLOWED_ID) : null;
        this.followerId = args.containsKey(BUNDLE_KEY_FOLLOWER_ID) ? args.getInt(BUNDLE_KEY_FOLLOWER_ID) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return (followedId == null ? 0 : followedId.hashCode()) ^
                (followerId == null ? 0 : followerId.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof FollowerId) && equals((FollowerId) other);
    }

    public boolean equals(FollowerId other)
    {
        return (other != null) &&
                (followedId == null ? other.followedId == null : followedId.equals(other.followedId)) &&
                (followerId == null ? other.followerId == null : followerId.equals(other.followerId));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == FollowerId.class)
        {
            return compareTo((FollowerId) o);
        }
        return o.getClass().getName().compareTo(FollowerId.class.getName());
    }

    public int compareTo(FollowerId other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        // TODO looks dangerous
        int followedIdComp = followedId.compareTo(other.followedId);
        if (followedIdComp != 0)
        {
            return followedIdComp;
        }

        return followerId.compareTo(other.followerId);
    }

    public boolean isValid()
    {
        return followedId != null && followerId != null;
    }

    public static boolean isValid(Bundle args)
    {
        return args != null &&
                args.containsKey(BUNDLE_KEY_FOLLOWED_ID) &&
                args.containsKey(BUNDLE_KEY_FOLLOWER_ID);
    }

    public void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_FOLLOWED_ID, followedId);
        args.putInt(BUNDLE_KEY_FOLLOWER_ID, followerId);
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @Override public String toString()
    {
        return String.format("[followedId=%s; followerId=%s]", followedId, followerId);
    }
}
