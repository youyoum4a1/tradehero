package com.ayondo.academy.persistence.watchlist;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTO;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTOList;
import com.ayondo.academy.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class WatchlistPositionCacheRx extends BaseDTOCacheRx<SecurityId, WatchlistPositionDTO>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public WatchlistPositionCacheRx(
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    // TODO add end-point

    @Override public void onNext(@NonNull SecurityId key, @NonNull WatchlistPositionDTO value)
    {
        if (value.securityDTO != null)
        {
            securityCompactCache.get().onNext(key, value.securityDTO);
        }
        super.onNext(key, value);
    }

    public void onNext(@NonNull WatchlistPositionDTOList watchlistPositionDTOs)
    {
        for (WatchlistPositionDTO watchlistPositionDTO : watchlistPositionDTOs)
        {
            //noinspection ConstantConditions
            onNext(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO);
        }
    }

    public void invalidate(@NonNull UserBaseKey concernedUser)
    {
        WatchlistPositionDTO cached;
        for (SecurityId key : snapshot().keySet())
        {
            cached = getCachedValue(key);
            if (cached != null
                    && concernedUser.key.equals(cached.userId))
            {
                invalidate(key);
            }
        }
    }
}
