package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class HeroListCache extends StraightDTOCache<UserBaseKey, HeroIdExtWrapper>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    protected UserServiceWrapper userServiceWrapper;
    protected HeroCache heroCache;

    //<editor-fold desc="Constructors">
    @Inject public HeroListCache(UserServiceWrapper userServiceWrapper, HeroCache heroCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.userServiceWrapper = userServiceWrapper;
        this.heroCache = heroCache;
    }
    //</editor-fold>

    @Override protected HeroIdExtWrapper fetch(UserBaseKey key) throws Throwable
    {
        HeroDTOList allHeros = userServiceWrapper.getHeroes(key);
        return putInternal(key, allHeros);
    }

    protected HeroIdExtWrapper putInternal(UserBaseKey key, HeroDTOList fleshedValues)
    {
        heroCache.put(key, fleshedValues);
        HeroIdExtWrapper created = new HeroIdExtWrapper(key, fleshedValues);
        put(key, created);
        return created;
    }
}
