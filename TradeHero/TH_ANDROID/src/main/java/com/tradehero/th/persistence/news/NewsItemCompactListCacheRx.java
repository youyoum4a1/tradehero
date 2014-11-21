package com.tradehero.th.persistence.news;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class NewsItemCompactListCacheRx extends BaseFetchDTOCacheRx<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
{
    private static final int DEFAULT_VALUE_SIZE = 100;
    private static final int DEFAULT_SUBJECT_SIZE = 10;

    @NonNull private final NewsServiceWrapper newsServiceWrapper;
    @NonNull private final Lazy<DiscussionCacheRx> discussionCacheLazy;

    //<editor-fold desc="Constructors">
    @Inject public NewsItemCompactListCacheRx(@ListCacheMaxSize IntPreference maxSize,
            @NonNull NewsServiceWrapper newsServiceWrapper,
            @NonNull Lazy<DiscussionCacheRx> discussionCacheLazy,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, DEFAULT_SUBJECT_SIZE, DEFAULT_SUBJECT_SIZE, dtoCacheUtil);
        this.newsServiceWrapper = newsServiceWrapper;
        this.discussionCacheLazy = discussionCacheLazy;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<PaginatedDTO<NewsItemCompactDTO>> fetch(@NonNull NewsItemListKey key)
    {
        return newsServiceWrapper.getNewsRx(key);
    }

    @Override public void onNext(@NonNull NewsItemListKey key, @NonNull PaginatedDTO<NewsItemCompactDTO> value)
    {
        discussionCacheLazy.get().onNext(value.getData());
        super.onNext(key, value);
    }
}
