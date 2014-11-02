package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.network.service.TradeServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class TradeCacheRx extends BaseFetchDTOCacheRx<OwnedTradeId, TradeDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 500;
    private static final int DEFAULT_MAX_SUBJECT_SIZE = 50;

    @NotNull private  TradeServiceWrapper tradeServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public TradeCacheRx(@NotNull TradeServiceWrapper tradeServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.tradeServiceWrapper = tradeServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<TradeDTO> fetch(@NotNull OwnedTradeId key)
    {
        return tradeServiceWrapper.getTradeRx(key);
    }

    public void onNext(@NotNull List<TradeDTO> tradeDTOs)
    {
        for (@NotNull TradeDTO tradeDTO : tradeDTOs)
        {
            onNext(tradeDTO.getOwnedTradeId(), tradeDTO);
        }
    }
}
