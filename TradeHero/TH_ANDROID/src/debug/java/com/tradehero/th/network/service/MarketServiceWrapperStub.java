package com.tradehero.th.network.service;

import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.api.market.SectorCompactDTO;
import com.tradehero.th.api.market.SectorCompactDTOList;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class MarketServiceWrapperStub extends MarketServiceWrapper
{
    //<editor-fold desc="Constructors">
    @Inject public MarketServiceWrapperStub(@NotNull MarketServiceRx marketServiceRx)
    {
        super(marketServiceRx);
    }
    //</editor-fold>

    @Override @NotNull public Observable<ExchangeSectorCompactListDTO> getAllExchangeSectorCompactRx()
    {
        return getExchangesRx()
                .map(exchanges -> {
                    ExchangeSectorCompactListDTO created = new ExchangeSectorCompactListDTO();
                    created.exchanges = exchanges;
                    created.sectors = new SectorCompactDTOList();
                    SectorCompactDTO sector1 = new SectorCompactDTO();
                    sector1.name = "Pharma";
                    created.sectors.add(sector1);
                    SectorCompactDTO sector2 = new SectorCompactDTO();
                    sector2.name = "IT";
                    created.sectors.add(sector2);
                    return created;
                });
    }
}
