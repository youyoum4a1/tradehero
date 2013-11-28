package com.tradehero.th.api.social;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/1/13 Time: 12:29 PM To change this template use File | Settings | File Templates. */
public class HeroId implements Comparable, DTOKey
{
    public final static String BUNDLE_KEY_HERO_ID = HeroId.class.getName() + ".heroId";
    public final static String BUNDLE_KEY_FOLLOWER_ID = HeroId.class.getName() + ".followerId";

    public final Integer heroId;
    public final Integer followerId;

    //<editor-fold desc="Constructors">
    public HeroId(final Integer heroId, final Integer followerId)
    {
        this.heroId = heroId;
        this.followerId = followerId;
    }

    public HeroId(Bundle args)
    {
        this.heroId = args.containsKey(BUNDLE_KEY_HERO_ID) ? args.getInt(BUNDLE_KEY_HERO_ID) : null;
        this.followerId = args.containsKey(BUNDLE_KEY_FOLLOWER_ID) ? args.getInt(BUNDLE_KEY_FOLLOWER_ID) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return (heroId == null ? 0 : heroId.hashCode()) ^
                (followerId == null ? 0 : followerId.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof HeroId) && equals((HeroId) other);
    }

    public boolean equals(HeroId other)
    {
        return (other != null) &&
                (heroId == null ? other.heroId == null : heroId.equals(other.heroId)) &&
                (followerId == null ? other.followerId == null : followerId.equals(other.followerId));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == HeroId.class)
        {
            return compareTo((HeroId) o);
        }
        return o.getClass().getName().compareTo(HeroId.class.getName());
    }

    public int compareTo(HeroId other)
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
        int heroIdComp = heroId.compareTo(other.heroId);
        if (heroIdComp != 0)
        {
            return heroIdComp;
        }

        return followerId.compareTo(other.followerId);
    }

    public boolean isValid()
    {
        return heroId != null && followerId != null;
    }

    public static boolean isValid(Bundle args)
    {
        return args != null &&
                args.containsKey(BUNDLE_KEY_HERO_ID) &&
                args.containsKey(BUNDLE_KEY_FOLLOWER_ID);
    }

    public void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_HERO_ID, heroId);
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
        return String.format("[heroId=%s; followerId=%s]", heroId, followerId);
    }
}
