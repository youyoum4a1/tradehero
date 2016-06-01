package com.ayondo.academy.api.discussion.newsfeed;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class NewsfeedCache extends BaseFetchDTOCacheRx<NewsfeedKey, NewsfeedDTO>
{
    private static final int DEFAULT_SIZE = 100;

    @Inject public NewsfeedCache(@NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(DEFAULT_SIZE, dtoCacheUtilRx);
    }

    @NonNull @Override protected Observable<NewsfeedDTO> fetch(@NonNull NewsfeedKey key)
    {
        throw new RuntimeException("Not implemented");
    }
}
