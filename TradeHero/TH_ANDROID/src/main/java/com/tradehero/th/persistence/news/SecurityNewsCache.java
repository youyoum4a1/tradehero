package com.tradehero.th.persistence.news;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.news.NewsCache;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.NewsServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

/**
 * Created by tradehero on 14-3-7.
 */
@Singleton
public class SecurityNewsCache extends StraightDTOCache<SecurityId, PaginatedDTO<NewsItemDTO>>
{
    public static final int DEFAULT_MAX_SIZE = 15;
    private final Lazy<NewsServiceWrapper> newsServiceWrapper;
    private final Lazy<NewsCache> newsCache;

    @Inject public SecurityNewsCache(Lazy<NewsCache> newsCache, Lazy<NewsServiceWrapper> newsServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.newsServiceWrapper = newsServiceWrapper;
        this.newsCache = newsCache;
    }

    @Override protected PaginatedDTO<NewsItemDTO> fetch(SecurityId key) throws Throwable
    {
        Timber.d("NewsHeadlineList fetch news, key:%s", key);
        return fetchSecurityNews(key.id);
    }

    private PaginatedDTO<NewsItemDTO> fetchSecurityNews(int securityId) throws Throwable
    {
        PaginatedDTO<NewsItemDTO> paginatedSecurityNews = newsServiceWrapper.get().getSecurityNews(securityId);

        // populate to NewsCache
        List<NewsItemDTO> newsList = paginatedSecurityNews.getData();
        if (newsList != null)
        {
            for (NewsItemDTO news: newsList)
            {
                newsCache.get().put(news.getNewsItemDTOKey(), news);
            }
        }

        return paginatedSecurityNews;
    }
}
