package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DTOProcessorDiscussionReply extends DTOProcessorDiscussionCreate
{
    @NotNull protected final DiscussionListCacheNew discussionListCache;
    @NotNull protected final DiscussionKey initiatingKey;

    //<editor-fold desc="Constructors">
    public DTOProcessorDiscussionReply(
            @NotNull DiscussionDTOFactory discussionDTOFactory,
            @NotNull CurrentUserId currentUserId,
            @NotNull DiscussionCache discussionCache,
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache,
            @Nullable DiscussionKey stubKey,
            @NotNull DiscussionListCacheNew discussionListCache,
            @NotNull DiscussionKey initiatingKey)
    {
        super(discussionDTOFactory, currentUserId, discussionCache, userMessagingRelationshipCache, stubKey);
        this.discussionListCache = discussionListCache;
        this.initiatingKey = initiatingKey;
    }
    //</editor-fold>

    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        @Nullable DiscussionDTO processed = super.process(discussionDTO);
        AbstractDiscussionCompactDTO cachedInitiating = discussionCache.get(initiatingKey);
        if (cachedInitiating != null)
        {
            if (stubKey == null || processed == null || !stubKey.id.equals(processed.id))
                // We have to check here otherwise the server request goes too early and we
                // do not end up with the right number of comments.
            {
                cachedInitiating.commentCount++;
                discussionCache.put(initiatingKey, cachedInitiating);
                discussionCache.getOrFetchAsync(initiatingKey, true);
            }
        }
        discussionListCache.getOrFetchAsyncWhereSameField(initiatingKey);
        return processed;
    }
}
