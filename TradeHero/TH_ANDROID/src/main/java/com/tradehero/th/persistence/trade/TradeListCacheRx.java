package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.network.service.TradeServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class TradeListCacheRx extends BaseFetchDTOCacheRx<OwnedPositionId, TradeDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull private final TradeServiceWrapper tradeServiceWrapper;
    @NotNull private final TradeCacheRx tradeCache;

    //<editor-fold desc="Constructors">
    @Inject public TradeListCacheRx(
            @NotNull TradeServiceWrapper tradeServiceWrapper,
            @NotNull TradeCacheRx tradeCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.tradeServiceWrapper = tradeServiceWrapper;
        this.tradeCache = tradeCache;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<TradeDTOList> fetch(@NotNull OwnedPositionId key)
    {
        return tradeServiceWrapper.getTradesRx(key);
    }

    @Override public void onNext(@NotNull OwnedPositionId key, @NotNull TradeDTOList value)
    {
        tradeCache.onNext(value);
        super.onNext(key, value);
    }
}
