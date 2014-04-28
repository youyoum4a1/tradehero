package com.tradehero.th.persistence.news;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.news.NewsCache;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.news.NewsHeadlineFragment;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCache;
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
    private final Lazy<SecurityCompactCache> securityCompactCache;

    @Inject public SecurityNewsCache(
            Lazy<NewsCache> newsCache,
            Lazy<SecurityCompactCache> securityCompactCache,
            Lazy<NewsServiceWrapper> newsServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.securityCompactCache = securityCompactCache;
        this.newsServiceWrapper = newsServiceWrapper;
        this.newsCache = newsCache;
    }

    @Override protected PaginatedDTO<NewsItemDTO> fetch(SecurityId key) throws Throwable
    {
        return fetchSecurityNews(key);
    }

    private PaginatedDTO<NewsItemDTO> fetchSecurityNews(SecurityId securityId) throws Throwable
    {

        SecurityCompactDTO security = securityCompactCache.get().getOrFetch(securityId);
        Timber.d("%s fetchSecurityNews consume: %s", NewsHeadlineFragment.TEST_KEY,(System.currentTimeMillis() - NewsHeadlineFragment.start));
        long start = System.currentTimeMillis();
        PaginatedDTO<NewsItemDTO> paginatedSecurityNews = newsServiceWrapper.get().getSecurityNews(security.id);
        Timber.d("%s request consume: %s", NewsHeadlineFragment.TEST_KEY,(System.currentTimeMillis() - start));
        // populate to NewsCache
        List<NewsItemDTO> newsList = paginatedSecurityNews.getData();
        if (newsList != null)
        {
            for (NewsItemDTO news: newsList)
            {
                newsCache.get().put(news.getDiscussionKey(), news);
            }
        }

        return paginatedSecurityNews;
    }
}
