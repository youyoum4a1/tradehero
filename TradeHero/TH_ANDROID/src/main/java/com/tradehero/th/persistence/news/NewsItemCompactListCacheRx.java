package com.ayondo.academy.persistence.news;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.ayondo.academy.api.news.NewsItemCompactDTO;
import com.ayondo.academy.api.news.key.NewsItemListKey;
import com.ayondo.academy.api.pagination.PaginatedDTO;
import com.ayondo.academy.network.service.NewsServiceWrapper;
import com.ayondo.academy.persistence.ListCacheMaxSize;
import com.ayondo.academy.persistence.discussion.DiscussionCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class NewsItemCompactListCacheRx extends BaseFetchDTOCacheRx<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
{
    private static final int DEFAULT_VALUE_SIZE = 100;

    @NonNull private final NewsServiceWrapper newsServiceWrapper;
    @NonNull private final Lazy<DiscussionCacheRx> discussionCacheLazy;

    //<editor-fold desc="Constructors">
    @Inject public NewsItemCompactListCacheRx(@ListCacheMaxSize IntPreference maxSize,
            @NonNull NewsServiceWrapper newsServiceWrapper,
            @NonNull Lazy<DiscussionCacheRx> discussionCacheLazy,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, dtoCacheUtil);
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
