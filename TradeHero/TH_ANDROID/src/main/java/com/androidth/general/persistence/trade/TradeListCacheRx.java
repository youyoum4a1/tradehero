package com.androidth.general.persistence.trade;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.position.OwnedPositionId;
import com.androidth.general.api.trade.TradeDTOList;
import com.androidth.general.network.service.TradeServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class TradeListCacheRx extends BaseFetchDTOCacheRx<OwnedPositionId, TradeDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;

    @NonNull private final TradeServiceWrapper tradeServiceWrapper;
    @NonNull private final TradeCacheRx tradeCache;

    //<editor-fold desc="Constructors">
    @Inject public TradeListCacheRx(
            @NonNull TradeServiceWrapper tradeServiceWrapper,
            @NonNull TradeCacheRx tradeCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.tradeServiceWrapper = tradeServiceWrapper;
        this.tradeCache = tradeCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<TradeDTOList> fetch(@NonNull OwnedPositionId key)
    {
        return tradeServiceWrapper.getTradesRx(key);
    }

    @Override public void onNext(@NonNull OwnedPositionId key, @NonNull TradeDTOList value)
    {
        tradeCache.onNext(value);
        super.onNext(key, value);
    }
}