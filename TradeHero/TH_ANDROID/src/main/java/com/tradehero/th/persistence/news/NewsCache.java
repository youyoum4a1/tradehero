package com.tradehero.th.persistence.news;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.NewsServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

/**
 * Created by tradehero on 14-3-7.
 */
@Singleton
public class NewsCache extends StraightDTOCache<SecurityId, PaginatedDTO<NewsItemDTO>>
{
    public static final int DEFAULT_MAX_SIZE = 15;
    private final Lazy<NewsServiceWrapper> newsServiceWrapper;

    @Inject public NewsCache(Lazy<NewsServiceWrapper> newsServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.newsServiceWrapper = newsServiceWrapper;
    }

    @Override protected PaginatedDTO<NewsItemDTO> fetch(SecurityId key) throws Throwable
    {
        Timber.d("NewsHeadlineList fetch news, key:%s", key);
        return fetchSecurityNews(key.id);
    }

    private PaginatedDTO<NewsItemDTO> fetchSecurityNews(int securityId) throws Throwable
    {
        return newsServiceWrapper.get().getSecurityNews(securityId);
    }
}
