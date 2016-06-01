package com.ayondo.academy.persistence.social;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.social.HeroDTOExtWrapper;
import com.ayondo.academy.api.social.HeroDTOList;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton @UserCache
public class HeroListCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, HeroDTOExtWrapper>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;

    @NonNull protected final UserServiceWrapper userServiceWrapper;
    @NonNull protected final HeroCacheRx heroCache;

    //<editor-fold desc="Constructors">
    @Inject public HeroListCacheRx(
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull HeroCacheRx heroCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.heroCache = heroCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<HeroDTOExtWrapper> fetch(@NonNull UserBaseKey key)
    {
        return userServiceWrapper.getHeroesRx(key)
                .map(new Func1<HeroDTOList, HeroDTOExtWrapper>()
                {
                    @Override public HeroDTOExtWrapper call(HeroDTOList heroList)
                    {
                        return new HeroDTOExtWrapper(heroList);
                    }
                });
    }

    @Override public void onNext(@NonNull UserBaseKey key, @NonNull HeroDTOExtWrapper value)
    {
        heroCache.onNext(key, value.allActiveHeroes);
        heroCache.onNext(key, value.activeFreeHeroes);
        heroCache.onNext(key, value.activePremiumHeroes);
        super.onNext(key, value);
    }
}
