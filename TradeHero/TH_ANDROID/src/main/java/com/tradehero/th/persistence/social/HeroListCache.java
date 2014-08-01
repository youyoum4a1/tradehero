package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class HeroListCache extends StraightCutDTOCacheNew<UserBaseKey, HeroDTOExtWrapper, HeroIdExtWrapper>
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

    @Override @NotNull public HeroDTOExtWrapper fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return new HeroDTOExtWrapper(userServiceWrapper.getHeroes(key));
    }

    @NotNull @Override protected HeroIdExtWrapper cutValue(@NotNull UserBaseKey key, @NotNull HeroDTOExtWrapper value)
    {
        heroCache.put(key, value.allActiveHeroes);
        heroCache.put(key, value.activeFreeHeroes);
        heroCache.put(key, value.activePremiumHeroes);
        return new HeroIdExtWrapper(value, key);
    }

    @Nullable @Override protected HeroDTOExtWrapper inflateValue(@NotNull UserBaseKey key, @Nullable HeroIdExtWrapper cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        HeroDTOExtWrapper value = new HeroDTOExtWrapper(
                heroCache.get(cutValue.allActiveHeroes),
                heroCache.get(cutValue.activeFreeHeroes),
                heroCache.get(cutValue.activePremiumHeroes));
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
