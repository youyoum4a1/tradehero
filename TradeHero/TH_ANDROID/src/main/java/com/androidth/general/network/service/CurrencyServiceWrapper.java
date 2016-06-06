package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import com.androidth.general.api.market.CurrencyDTO;
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

    @NonNull public Observable<List<CurrencyDTO>> getCurrenciesRx()
    {
        return currencyServiceRx.getCurrencies();
    }
}
