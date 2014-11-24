package com.tradehero.th.persistence.social;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class HeroListCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, HeroDTOExtWrapper>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NonNull protected final UserServiceWrapper userServiceWrapper;
    @NonNull protected final HeroCacheRx heroCache;

    //<editor-fold desc="Constructors">
    @Inject public HeroListCacheRx(
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull HeroCacheRx heroCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.heroCache = heroCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<HeroDTOExtWrapper> fetch(@NonNull UserBaseKey key)
    {
        return userServiceWrapper.getHeroesRx(key)
                .map(HeroDTOExtWrapper::new);
    }

    @Override public void onNext(@NonNull UserBaseKey key, @NonNull HeroDTOExtWrapper value)
    {
        heroCache.onNext(key, value.allActiveHeroes);
        heroCache.onNext(key, value.activeFreeHeroes);
        heroCache.onNext(key, value.activePremiumHeroes);
        super.onNext(key, value);
    }
}
