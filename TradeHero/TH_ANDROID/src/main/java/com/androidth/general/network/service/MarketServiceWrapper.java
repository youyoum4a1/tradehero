package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import com.androidth.general.api.market.ExchangeCompactDTOList;
import com.androidth.general.api.market.ExchangeListType;
import com.androidth.general.api.market.SectorCompactDTOList;
import com.androidth.general.api.market.SectorDTOList;
import com.androidth.general.api.market.SectorListType;
import com.androidth.general.api.security.CompositeExchangeSecurityDTO;
import com.fernandocejas.frodo.annotation.RxLogObservable;

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

    //<editor-fold desc="Get Exchanges with Top Securities">
    @NonNull public Observable<ExchangeCompactDTOList> getExchangesRx(@NonNull ExchangeListType exchangeListType)
    {
        return marketServiceRx.getExchanges(exchangeListType.topNStocks);
    }
    //</editor-fold>

    //<editor-fold desc="Get Sectors">
    @NonNull public Observable<SectorCompactDTOList> getSectorCompacts()
    {
        return marketServiceRx.getSectorCompacts();
    }

    @NonNull public Observable<SectorDTOList> getSectors(@NonNull SectorListType sectorListType)
    {
        return marketServiceRx.getSectors(sectorListType.topNStocks);
    }

    @NonNull
    public @RxLogObservable Observable<CompositeExchangeSecurityDTO> getLiveExchangeSecurityTypes()
    {
        return marketServiceRx.getLiveExchangeSecurityTypes();
    }
}
