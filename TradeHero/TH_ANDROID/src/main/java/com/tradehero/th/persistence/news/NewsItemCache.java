package com.tradehero.th.persistence.news;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class NewsItemCache extends StraightDTOCache<NewsItemDTOKey, NewsItemDTO>
{
    @NotNull private final Lazy<NewsServiceWrapper> newsServiceWrapper;
    @NotNull private final Lazy<DiscussionCache> discussionCache;

    @Inject public NewsItemCache(
            @NotNull Lazy<NewsServiceWrapper> newsServiceWrapper,
            @NotNull Lazy<DiscussionCache> discussionCache)
    {
        // Use discussionCache instead, wanted to set the size to 0, but it's not possible
        super(1);

        this.newsServiceWrapper = newsServiceWrapper;
        this.discussionCache = discussionCache;
    }

    @Override protected NewsItemDTO fetch(@NotNull NewsItemDTOKey newsItemDTOKey) throws Throwable
    {
        return newsServiceWrapper.get().getSecurityNewsDetail(newsItemDTOKey.id);
    }

    @Override public NewsItemDTO put(@NotNull NewsItemDTOKey key, @NotNull NewsItemDTO value)
    {
        discussionCache.get().put(key, value);
        return value;
    }

    @Override public NewsItemDTO get(NewsItemDTOKey key)
    {
        AbstractDiscussionCompactDTO abstractDiscussionDTO = discussionCache.get().get(key);

        return (abstractDiscussionDTO instanceof NewsItemDTO) ? (NewsItemDTO) abstractDiscussionDTO : null;
    }
}
