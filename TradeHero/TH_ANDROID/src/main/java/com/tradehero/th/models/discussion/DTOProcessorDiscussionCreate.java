package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DTOProcessorDiscussionCreate extends DTOProcessorDiscussion
{
    @NotNull protected final CurrentUserId currentUserId;
    @NotNull protected final DiscussionCacheRx discussionCache;
    @NotNull protected final UserMessagingRelationshipCacheRx userMessagingRelationshipCache;
    @Nullable protected final DiscussionKey stubKey;

    //<editor-fold desc="Constructors">
    public DTOProcessorDiscussionCreate(
            @NotNull DiscussionDTOFactory discussionDTOFactory,
            @NotNull CurrentUserId currentUserId,
            @NotNull DiscussionCacheRx discussionCache,
            @NotNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @Nullable DiscussionKey stubKey)
    {
        super(discussionDTOFactory);
        this.currentUserId = currentUserId;
        this.discussionCache = discussionCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.stubKey = stubKey;
    }
    //</editor-fold>

    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        @Nullable DiscussionDTO processed = super.process(discussionDTO);
        if (stubKey != null)
        {
            discussionCache.invalidate(stubKey);
        }
        if (processed != null && processed.userId <= 0)
        {
            // HACK
            processed.userId = currentUserId.toUserBaseKey().key;
        }
        if (processed != null)
        {
            discussionCache.onNext(processed.getDiscussionKey(), processed);
        }
        if (processed != null)
        {
            processed.stubKey = stubKey;
            userMessagingRelationshipCache.invalidate(new UserBaseKey(discussionDTO.userId));
        }
        return processed;
    }
}
