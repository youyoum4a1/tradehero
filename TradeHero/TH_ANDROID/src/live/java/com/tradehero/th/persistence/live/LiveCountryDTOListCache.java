package com.tradehero.th.persistence.live;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.th.api.live.LiveCountryDTOList;
import com.tradehero.th.api.live.LiveCountryListId;
import rx.Observable;

public class LiveCountryDTOListCache extends BaseFetchDTOCacheRx<LiveCountryListId, LiveCountryDTOList>
{
    protected LiveCountryDTOListCache(int valueSize, @NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(valueSize, dtoCacheUtilRx);
    }

    @NonNull @Override protected Observable<LiveCountryDTOList> fetch(@NonNull LiveCountryListId key)
    {
        return null;
    }
}
