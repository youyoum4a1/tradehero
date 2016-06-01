package com.ayondo.academy.persistence.trade;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.trade.OwnedTradeId;
import com.ayondo.academy.api.trade.TradeDTO;
import com.ayondo.academy.network.service.TradeServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class TradeCacheRx extends BaseFetchDTOCacheRx<OwnedTradeId, TradeDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 500;

    @NonNull private  TradeServiceWrapper tradeServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public TradeCacheRx(@NonNull TradeServiceWrapper tradeServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.tradeServiceWrapper = tradeServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<TradeDTO> fetch(@NonNull OwnedTradeId key)
    {
        return tradeServiceWrapper.getTradeRx(key);
    }

    public void onNext(@NonNull List<TradeDTO> tradeDTOs)
    {
        for (TradeDTO tradeDTO : tradeDTOs)
        {
            onNext(tradeDTO.getOwnedTradeId(), tradeDTO);
        }
    }
}
