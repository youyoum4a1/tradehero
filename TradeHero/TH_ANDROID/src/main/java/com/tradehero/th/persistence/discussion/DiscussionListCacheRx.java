package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.MessageDiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;

@Singleton @UserCache
public class DiscussionListCacheRx extends BaseFetchDTOCacheRx<DiscussionListKey, PaginatedDTO<DiscussionDTO>>
{
    private static final int DEFAULT_VALUE_SIZE = 30;
    private static final int DEFAULT_SUBJECT_SIZE = 3;

    @NotNull private final DiscussionCacheRx discussionCache;
    @NotNull private final DiscussionServiceWrapper discussionServiceWrapper;

    @Inject public DiscussionListCacheRx(
            @NotNull DiscussionServiceWrapper discussionServiceWrapper,
            @NotNull DiscussionCacheRx discussionCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, DEFAULT_SUBJECT_SIZE, DEFAULT_SUBJECT_SIZE, dtoCacheUtil);
        this.discussionServiceWrapper = discussionServiceWrapper;
        this.discussionCache = discussionCache;
    }

    @Override @NotNull protected Observable<PaginatedDTO<DiscussionDTO>> fetch(@NotNull DiscussionListKey discussionListKey)
    {
        if (discussionListKey instanceof MessageDiscussionListKey)
        {
            return discussionServiceWrapper.getMessageThreadRx((MessageDiscussionListKey) discussionListKey);
        }
        else if (discussionListKey instanceof PaginatedDiscussionListKey)
        {
            return discussionServiceWrapper.getDiscussionsRx((PaginatedDiscussionListKey) discussionListKey);
        }
        throw new IllegalStateException("Unhandled key " + discussionListKey);
    }

    @Override public void onNext(@NotNull DiscussionListKey key, @NotNull PaginatedDTO<DiscussionDTO> value)
    {
        List<DiscussionDTO> list = value.getData();
        if (list != null)
        {
            discussionCache.onNext(list);
        }
        super.onNext(key, value);
    }

    public void invalidateAllPagesFor(@Nullable DiscussionKey discussionKey)
    {
        for (DiscussionListKey discussionListKey : new ArrayList<>(snapshot().keySet()))
        {
            if (discussionListKey.equivalentFields(discussionKey))
            {
                invalidate(discussionListKey);
            }
        }
    }
    /**
     * TODO right
     * @param discussionType
     */
    public void invalidateAllForDiscussionType(DiscussionType discussionType)
    {
        for (DiscussionListKey discussionListKey : new ArrayList<>(snapshot().keySet()))
        {
            if (discussionListKey.inReplyToType == discussionType)
            {
                invalidate(discussionListKey);
            }
        }
    }

    public void getWhereSameField(@NotNull DiscussionKey originatingDiscussion)
    {
        for (DiscussionListKey key : snapshot().keySet())
        {
            if (key.equivalentFields(originatingDiscussion))
            {
                get(key);
            }
        }
    }
}
