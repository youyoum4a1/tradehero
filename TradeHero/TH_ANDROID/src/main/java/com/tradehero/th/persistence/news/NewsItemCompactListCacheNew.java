package com.tradehero.th.persistence.news;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class NewsItemCompactListCacheNew extends StraightDTOCacheNew<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
{
    @NotNull private final NewsServiceWrapper newsServiceWrapper;
    @NotNull private final Lazy<NewsItemCompactCacheNew> newsItemCompactCacheNew;

    @Inject public NewsItemCompactListCacheNew(@ListCacheMaxSize IntPreference maxSize,
            @NotNull NewsServiceWrapper newsServiceWrapper,
            @NotNull Lazy<NewsItemCompactCacheNew> newsItemCompactCacheNew)
    {
        super(maxSize.get());
        this.newsServiceWrapper = newsServiceWrapper;
        this.newsItemCompactCacheNew = newsItemCompactCacheNew;
    }

    @Override public PaginatedDTO<NewsItemCompactDTO> fetch(@NotNull NewsItemListKey key) throws Throwable
    {
        return newsServiceWrapper.getNews(key);
    }

    @Override public PaginatedDTO<NewsItemCompactDTO> put(
            @NotNull NewsItemListKey key,
            @NotNull PaginatedDTO<NewsItemCompactDTO> value)
    {
        newsItemCompactCacheNew.get().put(value.getData());
        return value;
    }
}
