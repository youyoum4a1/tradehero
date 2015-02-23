package com.tradehero.th.persistence.market;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.market.ExchangeSectorListDTO;
import com.tradehero.th.models.market.ExchangeSectorKey;
import com.tradehero.th.network.service.MarketServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class ExchangeSectorListCacheRx extends BaseFetchDTOCacheRx<ExchangeSectorKey, ExchangeSectorListDTO>
{
    private static final int MAX_SIZE = 1;

    @NonNull private final MarketServiceWrapper marketServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeSectorListCacheRx(
            @NonNull MarketServiceWrapper marketServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<ExchangeSectorListDTO> fetch(@NonNull ExchangeSectorKey key)
    {
        return marketServiceWrapper.getAllExchangeSectorCompactRx();
    }
}
