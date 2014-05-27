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

@Singleton public class NewsItemCompactListCacheNew extends StraightDTOCacheNew<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
{
    private final NewsServiceWrapper newsServiceWrapper;
    private final Lazy<NewsItemCompactCacheNew> newsItemCompactCacheNew;

    @Inject public NewsItemCompactListCacheNew(@ListCacheMaxSize IntPreference maxSize,
            NewsServiceWrapper newsServiceWrapper,
            Lazy<NewsItemCompactCacheNew> newsItemCompactCacheNew)
    {
        super(maxSize.get());
        this.newsServiceWrapper = newsServiceWrapper;
        this.newsItemCompactCacheNew = newsItemCompactCacheNew;
    }

    @Override public PaginatedDTO<NewsItemCompactDTO> fetch(NewsItemListKey key) throws Throwable
    {
        return newsServiceWrapper.getNews(key);
    }

    @Override public PaginatedDTO<NewsItemCompactDTO> put(NewsItemListKey key,
            PaginatedDTO<NewsItemCompactDTO> value)
    {
        if (value != null)
        {
            newsItemCompactCacheNew.get().put(value.getData());
        }
        return value;
    }
}
