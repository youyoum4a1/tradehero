package com.tradehero.th.persistence.live;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.live.LiveCountryDTOList;
import com.tradehero.th.api.live.LiveCountryListId;
import com.tradehero.th.network.service.LiveServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class LiveCountryDTOListCache extends BaseFetchDTOCacheRx<LiveCountryListId, LiveCountryDTOList>
{
    private static final int DEFAULT_SIZE = 5;

    @NonNull private final LiveServiceWrapper liveServiceWrapper;

    @Inject public LiveCountryDTOListCache(@NonNull DTOCacheUtilRx dtoCacheUtilRx,
            @NonNull LiveServiceWrapper liveServiceWrapper)
    {
        super(DEFAULT_SIZE, dtoCacheUtilRx);
        this.liveServiceWrapper = liveServiceWrapper;
    }

    @NonNull @Override protected Observable<LiveCountryDTOList> fetch(@NonNull LiveCountryListId key)
    {
        return liveServiceWrapper.getLiveCountryList(key);
    }
}
