package com.ayondo.academy.api.social.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTOKey;

public class FollowerHeroRelationId implements Comparable, DTOKey
{
    public final static String BUNDLE_KEY_HERO_ID = FollowerHeroRelationId.class.getName() + ".heroId";
    public final static String BUNDLE_KEY_FOLLOWER_ID = FollowerHeroRelationId.class.getName() + ".followerId";

    @Deprecated // TODO To remove with followerName
    public final static String BUNDLE_KEY_FOLLOWER_NAME = FollowerHeroRelationId.class.getName() + ".followerName";

    @NonNull public final Integer heroId;
    @NonNull public final Integer followerId;

    @Deprecated // TODO This is ugly
    public final String followerName;

    //<editor-fold desc="Constructors">
    public FollowerHeroRelationId(@NonNull final Integer heroId, @NonNull final Integer followerId)
    {
        this.heroId = heroId;
        this.followerId = followerId;
        this.followerName = null;
    }

    public FollowerHeroRelationId(@NonNull final Integer heroId, @NonNull final Integer followerId, String followerName)
    {
        this.heroId = heroId;
        this.followerId = followerId;
        this.followerName = followerName;
    }

    public FollowerHeroRelationId(Bundle args)
    {
        this.heroId = args.getInt(BUNDLE_KEY_HERO_ID);
        this.followerId = args.getInt(BUNDLE_KEY_FOLLOWER_ID);
        this.followerName = args.containsKey(BUNDLE_KEY_FOLLOWER_NAME) ? args.getString(BUNDLE_KEY_FOLLOWER_NAME) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return heroId.hashCode() ^
                followerId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof FollowerHeroRelationId) && equals((FollowerHeroRelationId) other);
    }

    public boolean equals(FollowerHeroRelationId other)
    {
        return (other != null) &&
                heroId.equals(other.heroId) &&
                followerId.equals(other.followerId);
    }

    @Override public int compareTo(@Nullable Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == FollowerHeroRelationId.class)
        {
            return compareTo((FollowerHeroRelationId) o);
        }
        return o.getClass().getName().compareTo(FollowerHeroRelationId.class.getName());
    }

    public int compareTo(@Nullable FollowerHeroRelationId other)
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
        int followedIdComp = heroId.compareTo(other.heroId);
        if (followedIdComp != 0)
        {
            return followedIdComp;
        }

        return followerId.compareTo(other.followerId);
    }

    public static boolean isValid(Bundle args)
    {
        return args != null &&
                args.containsKey(BUNDLE_KEY_HERO_ID) &&
                args.containsKey(BUNDLE_KEY_FOLLOWER_ID);
    }

    private void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_HERO_ID, heroId);
        args.putInt(BUNDLE_KEY_FOLLOWER_ID, followerId);
        args.putString(BUNDLE_KEY_FOLLOWER_NAME, followerName);
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @Override public String toString()
    {
        return String.format("[heroId=%s; followerId=%s]", heroId, followerId);
    }
}
