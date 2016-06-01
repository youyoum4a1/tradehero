package com.ayondo.academy.models.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.persistence.discussion.DiscussionCacheRx;
import com.ayondo.academy.persistence.discussion.DiscussionListCacheRx;
import com.ayondo.academy.persistence.user.UserMessagingRelationshipCacheRx;

public class DTOProcessorDiscussionReply extends DTOProcessorDiscussionCreate
{
    @NonNull protected final DiscussionListCacheRx discussionListCache;
    @NonNull protected final DiscussionKey initiatingKey;

    //<editor-fold desc="Constructors">
    public DTOProcessorDiscussionReply(
            @NonNull CurrentUserId currentUserId,
            @NonNull DiscussionCacheRx discussionCache,
            @NonNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @Nullable DiscussionKey stubKey,
            @NonNull DiscussionListCacheRx discussionListCache,
            @NonNull DiscussionKey initiatingKey)
    {
        super(currentUserId, discussionCache, userMessagingRelationshipCache, stubKey);
        this.discussionListCache = discussionListCache;
        this.initiatingKey = initiatingKey;
    }
    //</editor-fold>

    @Override public DiscussionDTO process(@NonNull DiscussionDTO discussionDTO)
    {
        DiscussionDTO processed = super.process(discussionDTO);
        AbstractDiscussionCompactDTO cachedInitiating = discussionCache.getCachedValue(initiatingKey);
        if (cachedInitiating != null)
        {
            if (stubKey == null || processed == null || !stubKey.id.equals(processed.id))
                // We have to check here otherwise the server request goes too early and we
                // do not end up with the right number of comments.
            {
                cachedInitiating.commentCount++;
                discussionCache.onNext(initiatingKey, cachedInitiating);
                discussionCache.get(initiatingKey);
            }
        }
        discussionListCache.getWhereSameField(initiatingKey);
        return processed;
    }
}
