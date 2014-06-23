package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class HeroListCache extends StraightDTOCacheNew<UserBaseKey, HeroIdExtWrapper>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    @NotNull protected final UserServiceWrapper userServiceWrapper;
    @NotNull protected final HeroCache heroCache;

    //<editor-fold desc="Constructors">
    @Inject public HeroListCache(
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull HeroCache heroCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.userServiceWrapper = userServiceWrapper;
        this.heroCache = heroCache;
    }
    //</editor-fold>

    @Override public HeroIdExtWrapper fetch(@NotNull UserBaseKey key) throws Throwable
    {
        HeroDTOList allHeros = userServiceWrapper.getHeroes(key);
        return putInternal(key, allHeros);
    }

    protected HeroIdExtWrapper putInternal(
            @NotNull UserBaseKey key,
            @NotNull HeroDTOList fleshedValues)
    {
        heroCache.put(key, fleshedValues);
        HeroIdExtWrapper created = new HeroIdExtWrapper(key, fleshedValues);
        put(key, created);
        return created;
    }
}
