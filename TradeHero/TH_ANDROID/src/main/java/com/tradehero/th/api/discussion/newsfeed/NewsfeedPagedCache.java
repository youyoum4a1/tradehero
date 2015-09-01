package com.tradehero.th.api.discussion.newsfeed;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class NewsfeedPagedCache extends BaseFetchDTOCacheRx<NewsfeedPagedDTOKey, NewsfeedDTOList>
{
    public static final int DEFAULT_SIZE = 10;
    @NonNull private final DiscussionServiceWrapper discussionServiceWrapper;
    @NonNull private final NewsfeedCache newsfeedCache;

    @Inject protected NewsfeedPagedCache(@NonNull DiscussionServiceWrapper discussionServiceWrapper, @NonNull NewsfeedCache newsfeedCache,
            @NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(DEFAULT_SIZE, dtoCacheUtilRx);
        this.discussionServiceWrapper = discussionServiceWrapper;
        this.newsfeedCache = newsfeedCache;
    }

    @NonNull @Override protected Observable<NewsfeedDTOList> fetch(@NonNull NewsfeedPagedDTOKey key)
    {
        return discussionServiceWrapper.getNewsfeed(key);
    }

    @Override public void onNext(@NonNull NewsfeedPagedDTOKey key, @NonNull NewsfeedDTOList value)
    {
        super.onNext(key, value);
        for (NewsfeedDTO dto : value)
        {
            newsfeedCache.onNext(dto.getKey(), dto);
        }
    }
}