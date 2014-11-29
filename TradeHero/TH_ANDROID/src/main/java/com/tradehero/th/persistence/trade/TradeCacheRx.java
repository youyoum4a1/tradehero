package com.tradehero.th.persistence.trade;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.network.service.TradeServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class TradeCacheRx extends BaseFetchDTOCacheRx<OwnedTradeId, TradeDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 500;
    private static final int DEFAULT_MAX_SUBJECT_SIZE = 50;

    @NonNull private  TradeServiceWrapper tradeServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public TradeCacheRx(@NonNull TradeServiceWrapper tradeServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
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
