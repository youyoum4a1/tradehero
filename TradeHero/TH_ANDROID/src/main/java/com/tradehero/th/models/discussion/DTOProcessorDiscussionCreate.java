package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DTOProcessorDiscussionCreate extends DTOProcessorDiscussion
{
    @NotNull protected final DiscussionCache discussionCache;
    @NotNull protected final UserMessagingRelationshipCache userMessagingRelationshipCache;
    @Nullable protected final DiscussionKey stubKey;

    //<editor-fold desc="Constructors">
    public DTOProcessorDiscussionCreate(
            @NotNull DiscussionDTOFactory discussionDTOFactory,
            @NotNull DiscussionCache discussionCache,
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache,
            @Nullable DiscussionKey stubKey)
    {
        super(discussionDTOFactory);
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
        if (processed != null)
        {
            discussionCache.put(processed.getDiscussionKey(), processed);
        }
        if (processed != null)
        {
            processed.stubKey = stubKey;
            userMessagingRelationshipCache.invalidate(new UserBaseKey(discussionDTO.userId));
        }
        return processed;
    }
}
