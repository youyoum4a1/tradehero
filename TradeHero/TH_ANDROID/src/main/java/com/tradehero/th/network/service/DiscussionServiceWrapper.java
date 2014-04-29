package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.discussion.key.MessageDiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.discussion.MiddleCallbackDiscussion;
import com.tradehero.th.models.discussion.MiddleCallbackPaginatedDiscussion;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

@Singleton public class DiscussionServiceWrapper
{
    private final DiscussionService discussionService;
    private final DiscussionServiceAsync discussionServiceAsync;
    private final DiscussionDTOFactory discussionDTOFactory;
    private final UserMessagingRelationshipCache userMessagingRelationshipCache;

    // It has to be lazy to avoid infinite dependency
    private final Lazy<DiscussionCache> discussionCache;

    @Inject public DiscussionServiceWrapper(
            DiscussionService discussionService,
            DiscussionServiceAsync discussionServiceAsync,
            DiscussionDTOFactory discussionDTOFactory,
            UserMessagingRelationshipCache userMessagingRelationshipCache,
            Lazy<DiscussionCache> discussionCache)
    {
        this.discussionService = discussionService;
        this.discussionServiceAsync = discussionServiceAsync;
        this.discussionDTOFactory = discussionDTOFactory;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.discussionCache = discussionCache;
    }

    // TODO add providers in RetrofitModule and RetrofitProtectedModule
    // TODO add methods based on DiscussionServiceAsync and MiddleCallback implementations

    //<editor-fold desc="Get Comment">
    public DiscussionDTO getComment(DiscussionKey discussionKey)
    {
        DiscussionDTO discussionDTO = discussionDTOFactory.createChildClass(discussionService.getComment(discussionKey.id));
        if (discussionDTO != null)
        {
            userMessagingRelationshipCache.invalidate(new UserBaseKey(discussionDTO.userId));
        }
        return discussionDTO;
    }

    public MiddleCallbackDiscussion getComment(DiscussionKey discussionKey, Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(
                callback,
                discussionDTOFactory,
                userMessagingRelationshipCache,
                discussionCache.get(),
                null);
        discussionServiceAsync.getComment(discussionKey.id, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Create Discussion">
    public DiscussionDTO createDiscussion(DiscussionFormDTO discussionFormDTO)
    {
        DiscussionDTO discussionDTO = discussionService.createDiscussion(discussionFormDTO);
        if (discussionDTO != null)
        {
            userMessagingRelationshipCache.invalidate(new UserBaseKey(discussionDTO.userId));
        }
        return discussionDTO;
    }

    public MiddleCallbackDiscussion createDiscussion(
            DiscussionFormDTO discussionFormDTO,
            Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(
                callback,
                discussionDTOFactory,
                userMessagingRelationshipCache,
                discussionCache.get(),
                discussionFormDTO.stubKey);
        if (discussionFormDTO.stubKey != null)
        {
            DiscussionDTO stub = discussionDTOFactory.createStub(discussionFormDTO);
            middleCallback.success(stub, null);
        }
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

    public PaginatedDTO<DiscussionDTO> getMessageThread(MessageDiscussionListKey discussionsKey)
    {
        return discussionService.getMessageThread(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.toMap());
    }

    public MiddleCallbackPaginatedDiscussion getMessageThread(
            MessageDiscussionListKey discussionsKey,
            Callback<PaginatedDTO<DiscussionDTO>> callback)
    {
        MiddleCallbackPaginatedDiscussion middleCallback = new MiddleCallbackPaginatedDiscussion(callback);
        discussionServiceAsync.getMessageThread(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.toMap(),
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Vote">
    public DiscussionDTO vote(DiscussionVoteKey discussionVoteKey)
    {
        DiscussionDTO discussionDTO = discussionService.vote(
                discussionVoteKey.inReplyToType,
                discussionVoteKey.inReplyToId,
                discussionVoteKey.voteDirection);
        if (discussionDTO != null)
        {
            userMessagingRelationshipCache.invalidate(new UserBaseKey(discussionDTO.userId));
        }
        return discussionDTO;
    }

    public MiddleCallbackDiscussion vote(DiscussionVoteKey discussionVoteKey, Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback =  new MiddleCallbackDiscussion(
                callback,
                discussionDTOFactory,
                userMessagingRelationshipCache,
                discussionCache.get(),
                null);
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
        DiscussionDTO discussionDTO = discussionService.share(
                discussionKey.inReplyToType,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO);
        if (discussionDTO != null)
        {
            userMessagingRelationshipCache.invalidate(new UserBaseKey(discussionDTO.userId));
        }

        return discussionDTO;
    }

    public MiddleCallbackDiscussion share(
            DiscussionListKey discussionKey,
            TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(
                callback,
                discussionDTOFactory,
                userMessagingRelationshipCache,
                discussionCache.get(),
                null);
        discussionServiceAsync.share(
                discussionKey.inReplyToType,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
