package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import com.tradehero.th.persistence.news.NewsItemCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class DiscussionCacheNew extends StraightDTOCacheNew<DiscussionKey, AbstractDiscussionCompactDTO>
{
    @NotNull private final DiscussionServiceWrapper discussionServiceWrapper;
    @NotNull private final Lazy<NewsItemCache> newsCache;

    @Inject public DiscussionCacheNew(
            @SingleCacheMaxSize IntPreference maxSize,
            @NotNull Lazy<NewsItemCache> newsCache,
            @NotNull DiscussionServiceWrapper discussionServiceWrapper)
    {
        super(maxSize.get());

        this.discussionServiceWrapper = discussionServiceWrapper;
        this.newsCache = newsCache;
    }

    @Override public AbstractDiscussionCompactDTO fetch(@NotNull DiscussionKey discussionKey) throws Throwable
    {
        if (discussionKey instanceof TimelineItemDTOKey)
        {
            // TODO
        }
        else if (discussionKey instanceof NewsItemDTOKey)
        {
            return newsCache.get().getOrFetch((NewsItemDTOKey) discussionKey);
        }
        else
        {
            return discussionServiceWrapper.getComment(discussionKey);
        }
        throw new IllegalArgumentException("Unhandled discussionKey: " + discussionKey);
    }

    @NotNull public DiscussionDTOList put(@NotNull List<? extends AbstractDiscussionCompactDTO> discussionList)
    {
        DiscussionDTOList<? super AbstractDiscussionCompactDTO> previous = new DiscussionDTOList<>();
        for (AbstractDiscussionCompactDTO discussionDTO : discussionList)
        {
            previous.add(put(discussionDTO.getDiscussionKey(), discussionDTO));
        }
        return previous;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public DiscussionDTOList<? super AbstractDiscussionCompactDTO> get(@Nullable List<DiscussionKey> discussionKeys)
    {
        if (discussionKeys == null)
        {
            return null;
        }
        DiscussionDTOList<? super AbstractDiscussionCompactDTO> dtos = new DiscussionDTOList<>();
        for (@NotNull DiscussionKey discussionKey : discussionKeys)
        {
            dtos.add(get(discussionKey));
        }
        return dtos;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public DiscussionDTOList<? super AbstractDiscussionCompactDTO> getOrFetch(@Nullable List<DiscussionKey> discussionKeys) throws Throwable
    {
        if (discussionKeys == null)
        {
            return null;
        }
        DiscussionDTOList<? super AbstractDiscussionCompactDTO> dtos = new DiscussionDTOList<>();
        for (@NotNull DiscussionKey discussionKey : discussionKeys)
        {
            dtos.add(getOrFetchSync(discussionKey));
        }
        return dtos;
    }

    public static interface DiscussionListener extends DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO>
    {
    }
}
