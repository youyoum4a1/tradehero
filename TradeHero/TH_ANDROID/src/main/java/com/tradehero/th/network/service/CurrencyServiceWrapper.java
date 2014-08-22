package com.tradehero.th.network.service;

import com.tradehero.th.api.market.CurrencyDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

@Singleton public class CurrencyServiceWrapper
{
    @NotNull private final CurrencyService currencyService;
    @NotNull private final CurrencyServiceAsync currencyServiceAsync;

    //<editor-fold desc="Constructors">
    @Inject public CurrencyServiceWrapper(
            @NotNull CurrencyService currencyService,
            @NotNull CurrencyServiceAsync currencyServiceAsync)
    {
        this.currencyService = currencyService;
        this.currencyServiceAsync = currencyServiceAsync;
    }
    //</editor-fold>

    public List<CurrencyDTO> getCurrencies()
    {
        return currencyService.getCurrencies();
    }

    public MiddleCallback<List<CurrencyDTO>> getCurrencies(
            Callback<List<CurrencyDTO>> callback)
    {
        MiddleCallback<List<CurrencyDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        currencyServiceAsync.getCurrencies(middleCallback);
        return middleCallback;
    }
}
