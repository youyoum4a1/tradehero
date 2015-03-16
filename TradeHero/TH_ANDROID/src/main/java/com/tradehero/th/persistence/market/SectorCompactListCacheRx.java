package com.tradehero.th.persistence.market;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.market.SectorCompactDTOList;
import com.tradehero.th.api.market.SectorListType;
import com.tradehero.th.network.service.MarketServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class SectorCompactListCacheRx extends BaseFetchDTOCacheRx<SectorListType, SectorCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1; // Be careful to increase when necessary

    @NonNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public SectorCompactListCacheRx(
            @NonNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<SectorCompactDTOList> fetch(@NonNull SectorListType key)
    {
        return marketServiceWrapper.get().getSectors();
    }
}
