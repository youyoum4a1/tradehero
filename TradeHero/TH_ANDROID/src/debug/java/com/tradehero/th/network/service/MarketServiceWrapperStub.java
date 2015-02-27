package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOUtilDebug;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.market.ExchangeSectorListDTO;
import com.tradehero.th.api.market.SectorCompactDTOList;
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

    @NonNull @Override public Observable<ExchangeCompactDTOList> getExchangesRx(@NonNull ExchangeListType exchangeListType)
    {
        return super.getExchangesRx(exchangeListType)
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

    @NonNull @Override public Observable<SectorCompactDTOList> getSectors()
    {
        return super.getSectors()
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SectorCompactDTOList>>()
                {
                    @Override public Observable<? extends SectorCompactDTOList> call(Throwable throwable)
                    {
                        return getAllExchangeSectorCompactRx().map(new Func1<ExchangeSectorListDTO, SectorCompactDTOList>()
                        {
                            @Override public SectorCompactDTOList call(ExchangeSectorListDTO exchangeSectorListDTO)
                            {
                                return new SectorCompactDTOList(exchangeSectorListDTO.sectors);
                            }
                        });
                    }
                });
    }

    @NonNull @Override public Observable<ExchangeSectorListDTO> getAllExchangeSectorCompactRx()
    {
        return super.getAllExchangeSectorCompactRx()
                .map(new Func1<ExchangeSectorListDTO, ExchangeSectorListDTO>()
                {
                    @Override public ExchangeSectorListDTO call(ExchangeSectorListDTO exchangeSectorListDTO)
                    {
                        for (ExchangeCompactDTO compact : exchangeSectorListDTO.exchanges)
                        {
                            ExchangeCompactDTOUtilDebug.tempPopulate(compact);
                        }

                        return exchangeSectorListDTO;
                    }
                })
                        // Put top stocks in exchanges. Eventually to be done on server
                .flatMap(new Func1<ExchangeSectorListDTO, Observable<ExchangeSectorListDTO>>()
                {
                    @Override public Observable<ExchangeSectorListDTO> call(final ExchangeSectorListDTO exchangeSectorListDTO)
                    {
                        return Observable.from(exchangeSectorListDTO.exchanges)
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
                                .map(new Func1<List<ExchangeDTO>, ExchangeSectorListDTO>()
                                {
                                    @Override public ExchangeSectorListDTO call(List<ExchangeDTO> exchangeDTOs)
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
                                        for (SectorDTO sector : exchangeSectorListDTO.sectors)
                                        {
                                            sector.topSecurities = list;
                                        }
                                        return exchangeSectorListDTO;
                                    }
                                });
                    }
                });
    }
}
