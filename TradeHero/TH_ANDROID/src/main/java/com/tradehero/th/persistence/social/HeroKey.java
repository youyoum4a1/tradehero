package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.users.UserBaseKey;

public class HeroKey implements DTOKey
{
    public UserBaseKey followerKey;
    public HeroType heroType;

    public HeroKey(UserBaseKey followerKey, HeroType followerType)
    {
        this.followerKey = followerKey;
        this.heroType = followerType;
    }

    @Override public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof HeroKey))
        {
            return false;
        }

        HeroKey that = (HeroKey) other;

        return (heroType == that.heroType) &&
                (followerKey == null ? that.followerKey == null : followerKey.equals(that.followerKey));
    }

    @Override public int hashCode()
    {
        return (followerKey == null ? 0 : followerKey.hashCode()) ^
                (heroType == null ? 0 : heroType.hashCode());
    }

    @Override public String toString()
    {
        return String.format("HeroKey %s, HeroType %s", followerKey, heroType);
    }
}
