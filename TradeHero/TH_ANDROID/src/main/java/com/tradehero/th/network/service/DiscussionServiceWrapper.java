package com.tradehero.th.network.service;

import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.discussion.key.GetDiscussionsKey;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 3/7/14.
 */
@Singleton public class DiscussionServiceWrapper
{
    public static final String TAG = DiscussionServiceWrapper.class.getSimpleName();

    private final DiscussionService discussionService;
    private final DiscussionServiceAsync discussionServiceAsync;

    @Inject public DiscussionServiceWrapper(DiscussionService discussionService, DiscussionServiceAsync discussionServiceAsync)
    {
        this.discussionService = discussionService;
        this.discussionServiceAsync = discussionServiceAsync;
    }

    // TODO add providers in RetrofitModule and RetrofitProtectedModule
    // TODO add methods based on DiscussionServiceAsync and MiddleCallback implementations

    public PaginatedDTO<DiscussionDTO> getDiscussions(GetDiscussionsKey discussionsKey)
    {
        return discussionService.getDiscussions(
                discussionsKey.inReplyToType.description,
                discussionsKey.inReplyToId,
                discussionsKey.page,
                discussionsKey.perPage);
    }

    public DiscussionDTO vote(DiscussionVoteKey discussionVoteKey)
    {
        return discussionService.vote(
                discussionVoteKey.inReplyToType.description,
                discussionVoteKey.inReplyToId,
                discussionVoteKey.voteDirection.description);
    }

    public DiscussionDTO share(DiscussionKey discussionKey, TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return discussionService.share(
                discussionKey.inReplyToType.description,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO);
    }
}
