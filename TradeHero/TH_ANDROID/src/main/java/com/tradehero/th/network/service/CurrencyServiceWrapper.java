package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.market.CurrencyDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class CurrencyServiceWrapper
{
    @NonNull private final CurrencyServiceRx currencyServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public CurrencyServiceWrapper(
            @NonNull CurrencyServiceRx currencyServiceRx)
    {
        this.currencyServiceRx = currencyServiceRx;
    }
    //</editor-fold>

    public Observable<List<CurrencyDTO>> getCurrenciesRx()
    {
        return currencyServiceRx.getCurrencies();
    }
}
