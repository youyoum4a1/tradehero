package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.users.UserBaseKey;

/**
 * Created by tradehero on 14-4-1.
 */
public class HeroKey implements DTOKey
{
    public UserBaseKey userBaseKey;

    public HeroType heroType;


    public HeroKey(UserBaseKey userBaseKey, HeroType followerType)
    {
        this.userBaseKey = userBaseKey;
        this.heroType = followerType;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof HeroKey)) return false;

        HeroKey that = (HeroKey) o;

        if (heroType != that.heroType) return false;
        if (userBaseKey != null ? !userBaseKey.equals(that.userBaseKey) : that.userBaseKey != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = userBaseKey != null ? userBaseKey.hashCode() : 0;
        result = 31 * result + (heroType != null ? heroType.hashCode() : 0);
        return result;
    }
}
