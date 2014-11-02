package com.tradehero.th.persistence.news;

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
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class NewsItemCompactListCacheRx extends BaseFetchDTOCacheRx<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
{
    private static final int DEFAULT_VALUE_SIZE = 100;
    private static final int DEFAULT_SUBJECT_SIZE = 10;

    @NotNull private final NewsServiceWrapper newsServiceWrapper;
    @NotNull private final Lazy<DiscussionCacheRx> discussionCacheLazy;

    //<editor-fold desc="Constructors">
    @Inject public NewsItemCompactListCacheRx(@ListCacheMaxSize IntPreference maxSize,
            @NotNull NewsServiceWrapper newsServiceWrapper,
            @NotNull Lazy<DiscussionCacheRx> discussionCacheLazy,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, DEFAULT_SUBJECT_SIZE, DEFAULT_SUBJECT_SIZE, dtoCacheUtil);
        this.newsServiceWrapper = newsServiceWrapper;
        this.discussionCacheLazy = discussionCacheLazy;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<PaginatedDTO<NewsItemCompactDTO>> fetch(@NotNull NewsItemListKey key)
    {
        return newsServiceWrapper.getNewsRx(key);
    }

    @Override public void onNext(@NotNull NewsItemListKey key, @NotNull PaginatedDTO<NewsItemCompactDTO> value)
    {
        discussionCacheLazy.get().onNext(value.getData());
        super.onNext(key, value);
    }
}
