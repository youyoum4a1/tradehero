package com.ayondo.academy.persistence.timeline;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.timeline.TimelineDTO;
import com.ayondo.academy.api.timeline.key.TimelineKey;
import com.ayondo.academy.network.service.UserTimelineServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class TimelineCacheRx extends BaseFetchDTOCacheRx<TimelineKey, TimelineDTO>
{
    private static final int DEFAULT_SIZE = 30;

    @NonNull private final UserTimelineServiceWrapper userTimelineServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject protected TimelineCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtilRx,
            @NonNull UserTimelineServiceWrapper userTimelineServiceWrapper)
    {
        super(DEFAULT_SIZE, dtoCacheUtilRx);
        this.userTimelineServiceWrapper = userTimelineServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<TimelineDTO> fetch(@NonNull TimelineKey key)
    {
        return userTimelineServiceWrapper.getTimelineBySectionRx(key);
    }
}
