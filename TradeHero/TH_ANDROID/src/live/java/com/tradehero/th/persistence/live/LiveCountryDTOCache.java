package com.tradehero.th.persistence.live;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.th.api.live.LiveCountryDTO;
import com.tradehero.th.api.live.LiveCountryId;

public class LiveCountryDTOCache extends BaseDTOCacheRx<LiveCountryId, LiveCountryDTO>
{
    private static final int MAX_SIZE = 150;

    protected LiveCountryDTOCache(@NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(MAX_SIZE, dtoCacheUtilRx);
    }
}
