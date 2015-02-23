package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeSectorListDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class MarketServiceWrapper
{
    @NonNull private final MarketServiceRx marketServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public MarketServiceWrapper(@NonNull MarketServiceRx marketServiceRx)
    {
        this.marketServiceRx = marketServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Exchanges">
    @NonNull public Observable<ExchangeCompactDTOList> getExchangesRx()
    {
        return marketServiceRx.getExchanges();
    }
    //</editor-fold>

    //<editor-fold desc="Get Exchange">
    @Deprecated // If never used
    @NonNull public Observable<ExchangeDTO> getExchangeRx(@NonNull ExchangeIntegerId exchangeId)
    {
        return marketServiceRx.getExchange(exchangeId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get All Exchange And Sectors Compact">
    @NonNull public Observable<ExchangeSectorListDTO> getAllExchangeSectorCompactRx()
    {
        return marketServiceRx.getAllExchangeSectorCompact();
    }
    //</editor-fold>
}
