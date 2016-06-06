package com.androidth.general.persistence.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.key.DiscussionKey;
import com.androidth.general.api.discussion.key.DiscussionListKey;
import com.androidth.general.api.discussion.key.MessageDiscussionListKey;
import com.androidth.general.api.discussion.key.PaginatedDiscussionListKey;
import com.androidth.general.api.pagination.PaginatedDTO;
import com.androidth.general.network.service.DiscussionServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class DiscussionListCacheRx extends BaseFetchDTOCacheRx<DiscussionListKey, PaginatedDTO<DiscussionDTO>>
{
    private static final int DEFAULT_VALUE_SIZE = 30;

    @NonNull private final DiscussionCacheRx discussionCache;
    @NonNull private final DiscussionServiceWrapper discussionServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionListCacheRx(
            @NonNull DiscussionServiceWrapper discussionServiceWrapper,
            @NonNull DiscussionCacheRx discussionCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, dtoCacheUtil);
        this.discussionServiceWrapper = discussionServiceWrapper;
        this.discussionCache = discussionCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<PaginatedDTO<DiscussionDTO>> fetch(@NonNull DiscussionListKey discussionListKey)
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

    @Override public void onNext(@NonNull DiscussionListKey key, @NonNull PaginatedDTO<DiscussionDTO> value)
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
    public void invalidateAllForDiscussionType(@NonNull DiscussionType discussionType)
    {
        for (DiscussionListKey discussionListKey : new ArrayList<>(snapshot().keySet()))
        {
            if (discussionListKey.inReplyToType == discussionType)
            {
                invalidate(discussionListKey);
            }
        }
    }

    public void getWhereSameField(@NonNull DiscussionKey originatingDiscussion)
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
