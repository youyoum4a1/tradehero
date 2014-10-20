package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.ReplyDiscussionFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.discussion.key.MessageDiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.discussion.DTOProcessorDiscussion;
import com.tradehero.th.models.discussion.DTOProcessorDiscussionReply;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class DiscussionServiceWrapper
{
    @NotNull private final DiscussionService discussionService;
    @NotNull private final DiscussionServiceAsync discussionServiceAsync;
    @NotNull private final DiscussionKeyFactory discussionKeyFactory;
    @NotNull private final DiscussionDTOFactory discussionDTOFactory;
    @NotNull private final CurrentUserId currentUserId;

    // It has to be lazy to avoid infinite dependency
    @NotNull private final Lazy<DiscussionListCacheNew> discussionListCache;
    @NotNull private final Lazy<DiscussionCache> discussionCache;
    @NotNull private final Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionServiceWrapper(
            @NotNull DiscussionService discussionService,
            @NotNull DiscussionServiceAsync discussionServiceAsync,
            @NotNull DiscussionKeyFactory discussionKeyFactory,
            @NotNull DiscussionDTOFactory discussionDTOFactory,
            @NotNull CurrentUserId currentUserId,
            @NotNull Lazy<DiscussionListCacheNew> discussionListCache,
            @NotNull Lazy<DiscussionCache> discussionCache,
            @NotNull Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache)
    {
        this.discussionService = discussionService;
        this.discussionServiceAsync = discussionServiceAsync;
        this.discussionKeyFactory = discussionKeyFactory;
        this.discussionDTOFactory = discussionDTOFactory;
        this.currentUserId = currentUserId;
        this.discussionCache = discussionCache;
        this.discussionListCache = discussionListCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
    }
    //</editor-fold>

    //<editor-fold desc="DTO Processors">
    @NotNull protected DTOProcessor<DiscussionDTO> createDiscussionProcessor()
    {
        return new DTOProcessorDiscussion(discussionDTOFactory);
    }

    @NotNull protected DTOProcessor<DiscussionDTO> createDiscussionReplyProcessor(@NotNull DiscussionKey initiatingKey,
            @Nullable DiscussionKey stubKey)
    {
        return new DTOProcessorDiscussionReply(
                discussionDTOFactory,
                currentUserId,
                discussionCache.get(),
                userMessagingRelationshipCache.get(),
                stubKey,
                discussionListCache.get(),
                initiatingKey);
    }
    //</editor-fold>

    // TODO add providers in RetrofitModule and RetrofitProtectedModule
    // TODO add methods based on DiscussionServiceAsync and MiddleCallback implementations

    //<editor-fold desc="Get Comment">
    @NotNull public DiscussionDTO getComment(@NotNull DiscussionKey discussionKey)
    {
        return createDiscussionProcessor().process(discussionService.getComment(discussionKey.id));
    }

    @NotNull public MiddleCallback<DiscussionDTO> getComment(
            @NotNull DiscussionKey discussionKey,
            @Nullable Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createDiscussionProcessor());
        discussionServiceAsync.getComment(discussionKey.id, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Create Discussion">
    @NotNull public DiscussionDTO createDiscussion(@NotNull DiscussionFormDTO discussionFormDTO)
    {
        if (discussionFormDTO instanceof ReplyDiscussionFormDTO)
        {
            return createDiscussionReplyProcessor(((ReplyDiscussionFormDTO) discussionFormDTO).getInitiatingDiscussionKey(), discussionFormDTO.stubKey)
                    .process(discussionService.createDiscussion(discussionFormDTO));
        }
        return postToTimeline(
                currentUserId.toUserBaseKey(),
                discussionFormDTO);
    }

    @NotNull public MiddleCallback<DiscussionDTO> createDiscussion(
            @NotNull DiscussionFormDTO discussionFormDTO,
            @Nullable Callback<DiscussionDTO> callback)
    {
        if (discussionFormDTO instanceof ReplyDiscussionFormDTO)
        {
            DTOProcessor<DiscussionDTO> processor = createDiscussionReplyProcessor(
                    ((ReplyDiscussionFormDTO) discussionFormDTO).getInitiatingDiscussionKey(),
                    discussionFormDTO.stubKey);
            MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                    callback, processor);
            if (discussionFormDTO.stubKey != null)
            {
                DiscussionDTO stub = discussionDTOFactory.createStub(discussionFormDTO);
                middleCallback.success(stub, null);
            }
            discussionServiceAsync.createDiscussion(discussionFormDTO, middleCallback);
            return middleCallback;
        }
        return postToTimeline(currentUserId.toUserBaseKey(), discussionFormDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Get Discussions">
    @Deprecated
    public PaginatedDTO<DiscussionDTO> getDiscussions(@NotNull PaginatedDiscussionListKey discussionsKey)
    {
        return discussionService.getDiscussions(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.page,
                discussionsKey.perPage);
    }

    @Deprecated
    public PaginatedDTO<DiscussionDTO> getPaginatedDiscussions(@NotNull DiscussionListKey discussionsKey)
    {
        return discussionService.getDiscussions(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.toMap());
    }

    public PaginatedDTO<DiscussionDTO> getMessageThread(@NotNull MessageDiscussionListKey discussionsKey)
    {
        return discussionService.getMessageThread(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.toMap());
    }

    @NotNull public MiddleCallback<PaginatedDTO<DiscussionDTO>> getMessageThread(
            @NotNull MessageDiscussionListKey discussionsKey,
            @Nullable Callback<PaginatedDTO<DiscussionDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<DiscussionDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        discussionServiceAsync.getMessageThread(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.toMap(),
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Vote">
    @NotNull public DiscussionDTO vote(@NotNull DiscussionVoteKey discussionVoteKey)
    {
        return createDiscussionReplyProcessor(
                discussionKeyFactory.create(discussionVoteKey.inReplyToType, discussionVoteKey.inReplyToId),
                null)
                .process(discussionService.vote(
                        discussionVoteKey.inReplyToType,
                        discussionVoteKey.inReplyToId,
                        discussionVoteKey.voteDirection));
    }

    @NotNull public MiddleCallback<DiscussionDTO> vote(
            @NotNull DiscussionVoteKey discussionVoteKey,
            @Nullable Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createDiscussionReplyProcessor(
                discussionKeyFactory.create(discussionVoteKey.inReplyToType, discussionVoteKey.inReplyToId),
                null));
        discussionServiceAsync.vote(
                discussionVoteKey.inReplyToType,
                discussionVoteKey.inReplyToId,
                discussionVoteKey.voteDirection,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Share">
    @NotNull public BaseResponseDTO share(
            @NotNull DiscussionListKey discussionKey,
            @NotNull TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return discussionService.share(
                discussionKey.inReplyToType,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO);
    }

    @NotNull public MiddleCallback<BaseResponseDTO> share(
            @NotNull DiscussionListKey discussionKey,
            @NotNull TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        MiddleCallback<BaseResponseDTO> middleCallback = new BaseMiddleCallback<>(callback);
        discussionServiceAsync.share(
                discussionKey.inReplyToType,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Post to Timeline">
    @NotNull public DiscussionDTO postToTimeline(
            @NotNull UserBaseKey userBaseKey,
            @NotNull DiscussionFormDTO discussionFormDTO)
    {
        return createDiscussionProcessor().process(
                discussionService.postToTimeline(
                        userBaseKey.key,
                        discussionFormDTO));
    }

    @NotNull public MiddleCallback<DiscussionDTO> postToTimeline(
            @NotNull UserBaseKey userBaseKey,
            @NotNull DiscussionFormDTO discussionFormDTO,
            @Nullable Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createDiscussionProcessor());
        if (discussionFormDTO.stubKey != null)
        {
            DiscussionDTO stub = discussionDTOFactory.createStub(discussionFormDTO);
            middleCallback.success(stub, null);
        }
        discussionServiceAsync.postToTimeline(
                userBaseKey.key,
                discussionFormDTO,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
