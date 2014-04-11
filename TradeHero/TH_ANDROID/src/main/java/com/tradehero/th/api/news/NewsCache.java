package com.tradehero.th.api.news;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
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
    private final Lazy<DiscussionCache> discussionCache;

    @Inject public NewsCache(
            Lazy<NewsServiceWrapper> newsServiceWrapper,
            Lazy<DiscussionCache> discussionCache
            )
    {
        // Use discussionCache instead, wanted to set the size to 0, but it's not possible
        super(1);

        this.newsServiceWrapper = newsServiceWrapper;
        this.discussionCache = discussionCache;
    }

    @Override protected NewsItemDTO fetch(NewsItemDTOKey newsItemDTOKey) throws Throwable
    {
        return newsServiceWrapper.get().getSecurityNewsDetail(newsItemDTOKey.id);
    }

    @Override public NewsItemDTO put(NewsItemDTOKey key, NewsItemDTO value)
    {
        discussionCache.get().put(key, value);
        return value;
    }

    @Override public NewsItemDTO get(NewsItemDTOKey key)
    {
        AbstractDiscussionDTO abstractDiscussionDTO = discussionCache.get().get(key);

        return (abstractDiscussionDTO instanceof NewsItemDTO) ? (NewsItemDTO) abstractDiscussionDTO : null;
    }
}
