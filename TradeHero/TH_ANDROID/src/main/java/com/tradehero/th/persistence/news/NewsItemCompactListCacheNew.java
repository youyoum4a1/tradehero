package com.tradehero.th.persistence.news;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class NewsItemCompactListCacheNew extends StraightDTOCacheNew<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
{
    @NotNull private final NewsServiceWrapper newsServiceWrapper;
    @NotNull private final Lazy<DiscussionCache> discussionCacheLazy;

    @Inject public NewsItemCompactListCacheNew(@ListCacheMaxSize IntPreference maxSize,
            @NotNull NewsServiceWrapper newsServiceWrapper,
            @NotNull Lazy<DiscussionCache> discussionCacheLazy)
    {
        super(maxSize.get());
        this.newsServiceWrapper = newsServiceWrapper;
        this.discussionCacheLazy = discussionCacheLazy;
    }

    @Override @NotNull public PaginatedDTO<NewsItemCompactDTO> fetch(@NotNull NewsItemListKey key) throws Throwable
    {
        return newsServiceWrapper.getNews(key);
    }

    @Override public PaginatedDTO<NewsItemCompactDTO> put(
            @NotNull NewsItemListKey key,
            @NotNull PaginatedDTO<NewsItemCompactDTO> value)
    {
        discussionCacheLazy.get().put(value.getData());
        return value;
    }
}
