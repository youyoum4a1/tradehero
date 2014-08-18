package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.network.service.MarketServiceWrapper;
import com.tradehero.th.persistence.DTOCacheUtil;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class ExchangeCompactListCache extends StraightDTOCacheNew<ExchangeListType, ExchangeCompactDTOList>
{
    public static final int DEFAULT_MAX_SIZE = 1; // Be careful to increase when necessary

    @NotNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;
    @NotNull private final Lazy<ExchangeIdCache> exchangeIdCache;
    @NotNull private final Lazy<DTOCacheUtil> dtoCacheUtil;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeCompactListCache(
            @NotNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NotNull Lazy<ExchangeIdCache> exchangeIdCache,
            @NotNull Lazy<DTOCacheUtil> dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE);
        this.marketServiceWrapper = marketServiceWrapper;
        this.exchangeIdCache = exchangeIdCache;
        this.dtoCacheUtil = dtoCacheUtil;
    }
    //</editor-fold>

    @Override @NotNull public ExchangeCompactDTOList fetch(@NotNull ExchangeListType key) throws Throwable
    {
        return marketServiceWrapper.get().getExchanges();
    }

    @Override @Nullable public ExchangeCompactDTOList put(@NotNull ExchangeListType key, @NotNull ExchangeCompactDTOList value)
    {
        exchangeIdCache.get().put(value);
        ExchangeCompactDTOList previous = super.put(key, value);
        dtoCacheUtil.get().preFetchTrending();
        return previous;
    }
}
