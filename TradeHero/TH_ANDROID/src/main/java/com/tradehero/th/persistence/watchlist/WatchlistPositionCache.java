package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Deprecated
@Singleton @UserCache public class WatchlistPositionCache extends StraightCutDTOCacheNew<SecurityId, WatchlistPositionDTO, WatchlistPositionCutDTO>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public WatchlistPositionCache(
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @Override @NonNull public WatchlistPositionDTO fetch(@NonNull SecurityId key) throws Throwable
    {
        throw new IllegalStateException("There is no fetch on WatchlistPositionCache");
    }

    @NonNull @Override protected WatchlistPositionCutDTO cutValue(@NonNull SecurityId key, @NonNull WatchlistPositionDTO value)
    {
        return new WatchlistPositionCutDTO(value, securityCompactCache.get());
    }

    @Nullable @Override protected WatchlistPositionDTO inflateValue(@NonNull SecurityId key, @Nullable WatchlistPositionCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.inflate(securityCompactCache.get());
    }

    @NonNull public WatchlistPositionDTOList put(@NonNull WatchlistPositionDTOList watchlistPositionDTOs)
    {
        WatchlistPositionDTOList previous = new WatchlistPositionDTOList();
        for (WatchlistPositionDTO watchlistPositionDTO : watchlistPositionDTOs)
        {
            //noinspection ConstantConditions
            previous.add(put(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO));
        }
        return previous;
    }

    @NonNull public WatchlistPositionDTOList get(@NonNull SecurityIdList securityIds)
    {
        WatchlistPositionDTOList cached = new WatchlistPositionDTOList();
        for (SecurityId securityId : securityIds)
        {
            cached.add(get(securityId));
        }
        return cached;
    }

    public void invalidate(@NonNull UserBaseKey concernedUser)
    {
        WatchlistPositionDTO cached;
        for (SecurityId key : snapshot().keySet())
        {
            cached = get(key);
            if (cached != null
                    && concernedUser.key.equals(cached.userId))
            {
                invalidate(key);
            }
        }
    }
}
