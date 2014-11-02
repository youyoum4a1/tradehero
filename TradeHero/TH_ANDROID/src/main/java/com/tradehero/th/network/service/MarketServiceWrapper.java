package com.tradehero.th.network.service;

import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class MarketServiceWrapper
{
    @NotNull private final MarketService marketService;
    @NotNull private final MarketServiceAsync marketServiceAsync;
    @NotNull private final MarketServiceRx marketServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public MarketServiceWrapper(
            @NotNull MarketService marketService,
            @NotNull MarketServiceAsync marketServiceAsync,
            @NotNull MarketServiceRx marketServiceRx)
    {
        this.marketService = marketService;
        this.marketServiceAsync = marketServiceAsync;
        this.marketServiceRx = marketServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Exchanges">
    public ExchangeCompactDTOList getExchanges()
    {
        return marketService.getExchanges();
    }

    @NotNull public Observable<ExchangeCompactDTOList> getExchangesRx()
    {
        return marketServiceRx.getExchanges();
    }
    //</editor-fold>

    //<editor-fold desc="Get Exchange">
    public ExchangeDTO getExchange(@NotNull ExchangeIntegerId exchangeId)
    {
        return marketService.getExchange(exchangeId.key);
    }

    public MiddleCallback<ExchangeDTO> getExchange(
            @NotNull ExchangeIntegerId exchangeId,
            @Nullable Callback<ExchangeDTO> callback)
    {
        MiddleCallback<ExchangeDTO> middleCallback = new BaseMiddleCallback<>(callback);
        marketServiceAsync.getExchange(exchangeId.key, middleCallback);
        return middleCallback;
    }

    @NotNull public Observable<ExchangeDTO> getExchangeRx(@NotNull ExchangeIntegerId exchangeId)
    {
        return marketServiceRx.getExchange(exchangeId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get All Exchange And Sectors Compact">
    @NotNull public ExchangeSectorCompactListDTO getAllExchangeSectorCompact()
    {
        return marketService.getAllExchangeSectorCompact();
    }

    @NotNull public Observable<ExchangeSectorCompactListDTO> getAllExchangeSectorCompactRx()
    {
        return marketServiceRx.getAllExchangeSectorCompact();
    }
    //</editor-fold>
}
