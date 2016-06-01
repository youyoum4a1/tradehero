package com.ayondo.academy.persistence.market;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.market.ExchangeCompactDTOList;
import com.ayondo.academy.api.market.ExchangeListType;
import com.ayondo.academy.network.service.MarketServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class ExchangeCompactListCacheRx extends BaseFetchDTOCacheRx<ExchangeListType, ExchangeCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 2; // Be careful to increase when necessary

    @NonNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;
    @NonNull private final Lazy<ExchangeIdCacheRx> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeCompactListCacheRx(
            @NonNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NonNull Lazy<ExchangeIdCacheRx> exchangeIdCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
        this.exchangeIdCache = exchangeIdCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<ExchangeCompactDTOList> fetch(@NonNull ExchangeListType key)
    {
        return marketServiceWrapper.get().getExchangesRx(key);
    }

    @Override public void onNext(@NonNull ExchangeListType key, @NonNull ExchangeCompactDTOList value)
    {
        exchangeIdCache.get().onNext(value);
        super.onNext(key, value);
    }
}
