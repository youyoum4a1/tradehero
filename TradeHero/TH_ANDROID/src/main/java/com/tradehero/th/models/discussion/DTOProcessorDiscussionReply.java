package com.tradehero.th.models.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import com.tradehero.th.persistence.discussion.DiscussionListCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;

public class DTOProcessorDiscussionReply extends DTOProcessorDiscussionCreate
{
    @NonNull protected final DiscussionListCacheRx discussionListCache;
    @NonNull protected final DiscussionKey initiatingKey;

    //<editor-fold desc="Constructors">
    public DTOProcessorDiscussionReply(
            @NonNull DiscussionDTOFactory discussionDTOFactory,
            @NonNull CurrentUserId currentUserId,
            @NonNull DiscussionCacheRx discussionCache,
            @NonNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @Nullable DiscussionKey stubKey,
            @NonNull DiscussionListCacheRx discussionListCache,
            @NonNull DiscussionKey initiatingKey)
    {
        super(discussionDTOFactory, currentUserId, discussionCache, userMessagingRelationshipCache, stubKey);
        this.discussionListCache = discussionListCache;
        this.initiatingKey = initiatingKey;
    }
    //</editor-fold>

    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        DiscussionDTO processed = super.process(discussionDTO);
        AbstractDiscussionCompactDTO cachedInitiating = discussionCache.getValue(initiatingKey);
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
