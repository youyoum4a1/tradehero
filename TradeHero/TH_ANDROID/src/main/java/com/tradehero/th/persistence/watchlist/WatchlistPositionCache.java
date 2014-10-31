package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @UserCache public class WatchlistPositionCache extends StraightCutDTOCacheNew<SecurityId, WatchlistPositionDTO, WatchlistPositionCutDTO>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @NotNull private final Lazy<SecurityCompactCache> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public WatchlistPositionCache(
            @NotNull Lazy<SecurityCompactCache> securityCompactCache,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @Override @NotNull public WatchlistPositionDTO fetch(@NotNull SecurityId key) throws Throwable
    {
        throw new IllegalStateException("There is no fetch on WatchlistPositionCache");
    }

    @NotNull @Override protected WatchlistPositionCutDTO cutValue(@NotNull SecurityId key, @NotNull WatchlistPositionDTO value)
    {
        return new WatchlistPositionCutDTO(value, securityCompactCache.get());
    }

    @Nullable @Override protected WatchlistPositionDTO inflateValue(@NotNull SecurityId key, @Nullable WatchlistPositionCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.inflate(securityCompactCache.get());
    }

    @NotNull public WatchlistPositionDTOList put(@NotNull WatchlistPositionDTOList watchlistPositionDTOs)
    {
        WatchlistPositionDTOList previous = new WatchlistPositionDTOList();
        for (WatchlistPositionDTO watchlistPositionDTO : watchlistPositionDTOs)
        {
            //noinspection ConstantConditions
            previous.add(put(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO));
        }
        return previous;
    }

    @NotNull public WatchlistPositionDTOList get(@NotNull SecurityIdList securityIds)
    {
        WatchlistPositionDTOList cached = new WatchlistPositionDTOList();
        for (SecurityId securityId : securityIds)
        {
            cached.add(get(securityId));
        }
        return cached;
    }

    public void invalidate(@NotNull UserBaseKey concernedUser)
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
