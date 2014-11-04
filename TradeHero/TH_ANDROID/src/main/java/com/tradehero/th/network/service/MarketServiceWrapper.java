package com.tradehero.th.network.service;

import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class MarketServiceWrapper
{
    @NotNull private final MarketServiceRx marketServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public MarketServiceWrapper(@NotNull MarketServiceRx marketServiceRx)
    {
        this.marketServiceRx = marketServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Exchanges">
    @NotNull public Observable<ExchangeCompactDTOList> getExchangesRx()
    {
        return marketServiceRx.getExchanges();
    }
    //</editor-fold>

    //<editor-fold desc="Get Exchange">
    @NotNull public Observable<ExchangeDTO> getExchangeRx(@NotNull ExchangeIntegerId exchangeId)
    {
        return marketServiceRx.getExchange(exchangeId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get All Exchange And Sectors Compact">
    @NotNull public Observable<ExchangeSectorCompactListDTO> getAllExchangeSectorCompactRx()
    {
        return marketServiceRx.getAllExchangeSectorCompact();
    }
    //</editor-fold>
}
