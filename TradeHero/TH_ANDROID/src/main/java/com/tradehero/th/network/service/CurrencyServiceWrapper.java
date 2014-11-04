package com.tradehero.th.network.service;

import com.tradehero.th.api.market.CurrencyDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class CurrencyServiceWrapper
{
    @NotNull private final CurrencyServiceRx currencyServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public CurrencyServiceWrapper(
            @NotNull CurrencyServiceRx currencyServiceRx)
    {
        this.currencyServiceRx = currencyServiceRx;
    }
    //</editor-fold>

    public Observable<List<CurrencyDTO>> getCurrenciesRx()
    {
        return currencyServiceRx.getCurrencies();
    }
}
