package com.tradehero.th.persistence.market;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.th.api.market.ExchangeCompactDTOUtil;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.network.service.MarketServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @SystemCache @Deprecated // If never used
public class ExchangeCacheRx extends BaseFetchDTOCacheRx<ExchangeIntegerId, ExchangeDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    @NonNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;
    @NonNull private final Lazy<ExchangeIdCacheRx> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeCacheRx(
            @NonNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NonNull Lazy<ExchangeIdCacheRx> exchangeIdCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
        this.exchangeIdCache = exchangeIdCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<ExchangeDTO> fetch(@NonNull ExchangeIntegerId key)
    {
        return marketServiceWrapper.get().getExchangeRx(key);
    }

    @Nullable @Override protected ExchangeDTO putValue(@NonNull ExchangeIntegerId key, @NonNull ExchangeDTO value)
    {
        ExchangeCompactDTOUtil.tempPopulate(value);
        return super.putValue(key, value);
    }

    @Override public void onNext(@NonNull ExchangeIntegerId key, @NonNull ExchangeDTO value)
    {
        exchangeIdCache.get().onNext(value.getExchangeStringId(), key);
        super.onNext(key, value);
    }
}
