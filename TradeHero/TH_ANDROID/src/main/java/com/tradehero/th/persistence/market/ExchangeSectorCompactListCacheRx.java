package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.models.market.ExchangeSectorCompactKey;
import com.tradehero.th.network.service.MarketServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class ExchangeSectorCompactListCacheRx extends BaseFetchDTOCacheRx<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO>
{
    private static final int MAX_SIZE = 1;

    @NotNull private final MarketServiceWrapper marketServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeSectorCompactListCacheRx(
            @NotNull MarketServiceWrapper marketServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(MAX_SIZE, MAX_SIZE, MAX_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override protected Observable<ExchangeSectorCompactListDTO> fetch(@NotNull ExchangeSectorCompactKey key)
    {
        return marketServiceWrapper.getAllExchangeSectorCompactRx();
    }
}
