package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.models.market.ExchangeSectorCompactKey;
import com.tradehero.th.network.service.MarketServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache
public class ExchangeSectorCompactListCache extends StraightDTOCacheNew<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO>
{
    private static final int MAX_SIZE = 1;

    @NotNull private final MarketServiceWrapper marketServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeSectorCompactListCache(
            @NotNull MarketServiceWrapper marketServiceWrapper,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override public ExchangeSectorCompactListDTO fetch(@NotNull ExchangeSectorCompactKey key) throws Throwable
    {
        return marketServiceWrapper.getAllExchangeSectorCompact();
    }
}
