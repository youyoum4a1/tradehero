package com.ayondo.academy.persistence.market;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.market.SectorDTOList;
import com.ayondo.academy.api.market.SectorListType;
import com.ayondo.academy.network.service.MarketServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class SectorListCacheRx extends BaseFetchDTOCacheRx<SectorListType, SectorDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1; // Be careful to increase when necessary

    @NonNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public SectorListCacheRx(
            @NonNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<SectorDTOList> fetch(@NonNull SectorListType key)
    {
        return marketServiceWrapper.get().getSectors(key);
    }
}
