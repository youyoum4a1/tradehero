package com.androidth.general.persistence.trade;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.trade.OwnedTradeId;
import com.androidth.general.api.trade.TradeDTO;
import com.androidth.general.network.service.TradeServiceWrapper;
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
