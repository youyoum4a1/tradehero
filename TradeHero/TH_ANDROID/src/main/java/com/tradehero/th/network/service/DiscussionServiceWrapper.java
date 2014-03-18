package com.tradehero.th.network.service;

import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.discussion.key.GetDiscussionsKey;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

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

    public MiddleCallback<DiscussionDTO> createDiscussion(DiscussionDTO discussionDTO, Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new MiddleCallback<>(callback);
        discussionServiceAsync.createDiscussion(discussionDTO, middleCallback);
        return middleCallback;
    }

    public MiddleCallback<DiscussionDTO> vote(DiscussionVoteKey discussionVoteKey, Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new MiddleCallback<>(callback);
        discussionServiceAsync.vote(
                discussionVoteKey.inReplyToType.description,
                discussionVoteKey.inReplyToId,
                discussionVoteKey.voteDirection.description,
                middleCallback);
        return middleCallback;
    }

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

    public void share(DiscussionKey discussionKey, TimelineItemShareRequestDTO timelineItemShareRequestDTO, Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new MiddleCallback<DiscussionDTO>(callback);
        discussionServiceAsync.share(
                discussionKey.inReplyToType.description,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO,middleCallback);
    }
}
