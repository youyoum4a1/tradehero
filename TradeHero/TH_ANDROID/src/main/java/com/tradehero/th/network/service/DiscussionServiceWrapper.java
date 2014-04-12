package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.discussion.key.RangedDiscussionListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.RangedDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.models.discussion.MiddleCallbackDiscussion;
import com.tradehero.th.models.discussion.MiddleCallbackRangedDiscussion;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

@Singleton public class DiscussionServiceWrapper
{
    public static final String TAG = DiscussionServiceWrapper.class.getSimpleName();

    private final DiscussionService discussionService;
    private final DiscussionServiceAsync discussionServiceAsync;
    private final DiscussionDTOFactory discussionDTOFactory;

    @Inject public DiscussionServiceWrapper(
            DiscussionService discussionService,
            DiscussionServiceAsync discussionServiceAsync,
            DiscussionDTOFactory discussionDTOFactory)
    {
        this.discussionService = discussionService;
        this.discussionServiceAsync = discussionServiceAsync;
        this.discussionDTOFactory = discussionDTOFactory;
    }

    // TODO add providers in RetrofitModule and RetrofitProtectedModule
    // TODO add methods based on DiscussionServiceAsync and MiddleCallback implementations

    //<editor-fold desc="Get Comment">
    public DiscussionDTO getComment(DiscussionKey discussionKey)
    {
        return discussionDTOFactory.createChildClass(discussionService.getComment(discussionKey.id));
    }

    public MiddleCallbackDiscussion getComment(DiscussionKey discussionKey, Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(callback, discussionDTOFactory);
        discussionServiceAsync.getComment(discussionKey.id, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Create Discussion">
    public DiscussionDTO createDiscussion(DiscussionFormDTO discussionFormDTO)
    {
        return discussionService.createDiscussion(discussionFormDTO);
    }

    public MiddleCallback<DiscussionDTO> createDiscussion(DiscussionFormDTO discussionFormDTO, Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new MiddleCallback<>(callback);
        discussionServiceAsync.createDiscussion(discussionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Discussions">
    @Deprecated
    public PaginatedDTO<DiscussionDTO> getDiscussions(PaginatedDiscussionListKey discussionsKey)
    {
        return discussionService.getDiscussions(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.page,
                discussionsKey.perPage);
    }

    @Deprecated
    public PaginatedDTO<DiscussionDTO> getPaginatedDiscussions(DiscussionListKey discussionsKey)
    {
        return discussionService.getDiscussions(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.toMap());
    }

    public RangedDTO<DiscussionDTO, DiscussionDTOList<DiscussionDTO>> getDiscussions(DiscussionListKey discussionsKey)
    {
        if (discussionsKey instanceof RangedDiscussionListKey)
        {
            return getDiscussions((RangedDiscussionListKey) discussionsKey);
        }
        throw new IllegalArgumentException("Unhandled type " + discussionsKey.getClass().getName());
    }

    public RangedDTO<DiscussionDTO, DiscussionDTOList<DiscussionDTO>> getDiscussions(RangedDiscussionListKey discussionsKey)
    {
        return discussionService.getMessageThread(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.maxCount,
                discussionsKey.maxId,
                discussionsKey.minId);
    }

    public MiddleCallbackRangedDiscussion getDiscussions(
            DiscussionListKey discussionsKey,
            Callback<RangedDTO<DiscussionDTO, DiscussionDTOList<DiscussionDTO>>> callback)
    {
        if (discussionsKey instanceof RangedDiscussionListKey)
        {
            return getDiscussions((RangedDiscussionListKey) discussionsKey, callback);
        }
        throw new IllegalArgumentException("Unhandled type " + discussionsKey.getClass().getName());
    }

    public MiddleCallbackRangedDiscussion getDiscussions(
            RangedDiscussionListKey discussionsKey,
            Callback<RangedDTO<DiscussionDTO, DiscussionDTOList<DiscussionDTO>>> callback)
    {
        MiddleCallbackRangedDiscussion middleCallback = new MiddleCallbackRangedDiscussion(callback);
        discussionServiceAsync.getMessageThread(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.maxCount,
                discussionsKey.maxId,
                discussionsKey.minId,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Vote">
    public DiscussionDTO vote(DiscussionVoteKey discussionVoteKey)
    {
        return discussionService.vote(
                discussionVoteKey.inReplyToType,
                discussionVoteKey.inReplyToId,
                discussionVoteKey.voteDirection);
    }

    public MiddleCallbackDiscussion vote(DiscussionVoteKey discussionVoteKey, Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback =  new MiddleCallbackDiscussion(callback, discussionDTOFactory);
        discussionServiceAsync.vote(
                discussionVoteKey.inReplyToType,
                discussionVoteKey.inReplyToId,
                discussionVoteKey.voteDirection,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Share">
    public DiscussionDTO share(DiscussionListKey discussionKey, TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return discussionService.share(
                discussionKey.inReplyToType,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO);
    }

    public MiddleCallbackDiscussion share(
            DiscussionListKey discussionKey,
            TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(callback, discussionDTOFactory);
        discussionServiceAsync.share(
                discussionKey.inReplyToType,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
