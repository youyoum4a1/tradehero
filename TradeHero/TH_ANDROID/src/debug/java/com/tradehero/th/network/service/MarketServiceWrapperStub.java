package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOUtilDebug;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.api.market.SectorDTO;
import com.tradehero.th.api.market.SecuritySuperCompactDTOList;
import com.tradehero.th.api.market.SecuritySuperCompactFactory;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class MarketServiceWrapperStub extends MarketServiceWrapper
{
    private static final int MAX_TOP_STOCKS = 6;

    @NonNull final Lazy<SecurityCompactListCacheRx> securityCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public MarketServiceWrapperStub(
            @NonNull MarketServiceRx marketServiceRx,
            @NonNull Lazy<SecurityCompactListCacheRx> securityCompactListCache)
    {
        super(marketServiceRx);
        this.securityCompactListCache = securityCompactListCache;
    }
    //</editor-fold>

    @NonNull @Override public Observable<ExchangeCompactDTOList> getExchangesRx()
    {
        return super.getExchangesRx()
                .map(new Func1<ExchangeCompactDTOList, ExchangeCompactDTOList>()
                {
                    @Override public ExchangeCompactDTOList call(ExchangeCompactDTOList exchangeCompactDTOs)
                    {
                        for (ExchangeCompactDTO exchange : exchangeCompactDTOs)
                        {
                            ExchangeCompactDTOUtilDebug.tempPopulate(exchange);
                        }

                        return exchangeCompactDTOs;
                    }
                });
    }

    @NonNull @Override public Observable<ExchangeDTO> getExchangeRx(@NonNull ExchangeIntegerId exchangeId)
    {
        return super.getExchangeRx(exchangeId)
                .map(new Func1<ExchangeDTO, ExchangeDTO>()
                {
                    @Override public ExchangeDTO call(ExchangeDTO exchangeDTO)
                    {
                        ExchangeCompactDTOUtilDebug.tempPopulate(exchangeDTO);
                        return null;
                    }
                });
    }

    @NonNull @Override public Observable<ExchangeSectorCompactListDTO> getAllExchangeSectorCompactRx()
    {
        return super.getAllExchangeSectorCompactRx()
                .map(new Func1<ExchangeSectorCompactListDTO, ExchangeSectorCompactListDTO>()
                {
                    @Override public ExchangeSectorCompactListDTO call(ExchangeSectorCompactListDTO exchangeSectorCompactListDTO)
                    {
                        for (ExchangeCompactDTO compact : exchangeSectorCompactListDTO.exchanges)
                        {
                            ExchangeCompactDTOUtilDebug.tempPopulate(compact);
                        }

                        return exchangeSectorCompactListDTO;
                    }
                })
                        // Put top stocks in exchanges. Eventually to be done on server
                .flatMap(new Func1<ExchangeSectorCompactListDTO, Observable<ExchangeSectorCompactListDTO>>()
                {
                    @Override public Observable<ExchangeSectorCompactListDTO> call(final ExchangeSectorCompactListDTO exchangeSectorCompactListDTO)
                    {
                        return Observable.from(exchangeSectorCompactListDTO.exchanges)
                                .flatMap(new Func1<ExchangeDTO, Observable<ExchangeDTO>>()
                                {
                                    @Override public Observable<ExchangeDTO> call(final ExchangeDTO exchange)
                                    {
                                        return securityCompactListCache.get()
                                                .getOne(new TrendingBasicSecurityListType(exchange.name, 1, MAX_TOP_STOCKS))
                                                .map(new Func1<Pair<SecurityListType, SecurityCompactDTOList>, ExchangeDTO>()
                                                {
                                                    @Override public ExchangeDTO call(Pair<SecurityListType, SecurityCompactDTOList> pair)
                                                    {
                                                        exchange.topSecurities = SecuritySuperCompactFactory.create(pair.second);
                                                        return exchange;
                                                    }
                                                });
                                    }
                                })
                                .toList()
                                .map(new Func1<List<ExchangeDTO>, ExchangeSectorCompactListDTO>()
                                {
                                    @Override public ExchangeSectorCompactListDTO call(List<ExchangeDTO> exchangeDTOs)
                                    {
                                        SecuritySuperCompactDTOList list = new SecuritySuperCompactDTOList();
                                        int count = MAX_TOP_STOCKS;
                                        for (ExchangeDTO exchange : exchangeDTOs)
                                        {
                                            list.addAll(exchange.topSecurities);
                                            if (--count <= 0)
                                            {
                                                break;
                                            }
                                        }
                                        for (SectorDTO sector : exchangeSectorCompactListDTO.sectors)
                                        {
                                            sector.topSecurities = list;
                                        }
                                        return exchangeSectorCompactListDTO;
                                    }
                                });
                    }
                });
    }
}
