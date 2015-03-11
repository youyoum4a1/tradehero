package com.tradehero.th.network.service;

import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class MarketServiceWrapper
{
    @NotNull private final MarketService marketService;
    @NotNull private final MarketServiceAsync marketServiceAsync;

    //<editor-fold desc="Constructors">
    @Inject public MarketServiceWrapper(
            @NotNull MarketService marketService,
            @NotNull MarketServiceAsync marketServiceAsync)
    {
        this.marketService = marketService;
        this.marketServiceAsync = marketServiceAsync;
    }
    //</editor-fold>

    //<editor-fold desc="Get Exchanges">
    public ExchangeCompactDTOList getExchanges()
    {
        return marketService.getExchanges();
    }

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
    //</editor-fold>
}
