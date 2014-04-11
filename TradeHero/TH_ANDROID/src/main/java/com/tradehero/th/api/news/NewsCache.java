package com.tradehero.th.api.news;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by tho on 3/27/2014.
 */
@Singleton
public class NewsCache extends StraightDTOCache<NewsItemDTOKey, NewsItemDTO>
{
    private final Lazy<NewsServiceWrapper> newsServiceWrapper;

    @Inject public NewsCache(@SingleCacheMaxSize IntPreference cacheMaxSize, Lazy<NewsServiceWrapper> newsServiceWrapper)
    {
        super(cacheMaxSize.get());
        this.newsServiceWrapper = newsServiceWrapper;
    }

    @Override protected NewsItemDTO fetch(NewsItemDTOKey newsItemDTOKey) throws Throwable
    {
        return newsServiceWrapper.get().getSecurityNewsDetail(newsItemDTOKey.id);
    }
}
