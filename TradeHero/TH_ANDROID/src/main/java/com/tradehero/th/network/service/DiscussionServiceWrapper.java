package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.discussion.key.MessageDiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.discussion.DTOProcessorDiscussion;
import com.tradehero.th.models.discussion.DTOProcessorDiscussionCreate;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.discussion.DiscussionCache;
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

    // It has to be lazy to avoid infinite dependency
    @NotNull private final Lazy<DiscussionCache> discussionCache;
    @NotNull private final Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionServiceWrapper(
            @NotNull DiscussionService discussionService,
            @NotNull DiscussionServiceAsync discussionServiceAsync,
            @NotNull DiscussionKeyFactory discussionKeyFactory,
            @NotNull DiscussionDTOFactory discussionDTOFactory,
            @NotNull Lazy<DiscussionCache> discussionCache,
            @NotNull Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache)
    {
        this.discussionService = discussionService;
        this.discussionServiceAsync = discussionServiceAsync;
        this.discussionKeyFactory = discussionKeyFactory;
        this.discussionDTOFactory = discussionDTOFactory;
        this.discussionCache = discussionCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
    }
    //</editor-fold>

    //<editor-fold desc="DTO Processors">
    @NotNull protected DTOProcessor<DiscussionDTO> createDiscussionProcessor()
    {
        return new DTOProcessorDiscussion(discussionDTOFactory);
    }

    @NotNull protected DTOProcessor<DiscussionDTO> createDiscussionCreateProcessor(@NotNull DiscussionKey initiatingKey, @Nullable DiscussionKey stubKey)
    {
        return new DTOProcessorDiscussionCreate(
                discussionDTOFactory,
                discussionCache.get(),
                userMessagingRelationshipCache.get(),
                initiatingKey,
                stubKey);
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
        return createDiscussionCreateProcessor(discussionFormDTO.getInitiatingDiscussionKey(), discussionFormDTO.stubKey).process(
            discussionService.createDiscussion(discussionFormDTO));
    }

    @NotNull public MiddleCallback<DiscussionDTO> createDiscussion(
            @NotNull DiscussionFormDTO discussionFormDTO,
            @Nullable Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createDiscussionCreateProcessor(discussionFormDTO.getInitiatingDiscussionKey(), discussionFormDTO.stubKey));
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
        return createDiscussionCreateProcessor(
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
        MiddleCallback<DiscussionDTO> middleCallback =  new BaseMiddleCallback<>(
                callback, createDiscussionCreateProcessor(
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
    @NotNull public DiscussionDTO share(
            @NotNull DiscussionListKey discussionKey,
            @NotNull TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return createDiscussionCreateProcessor(
                discussionKeyFactory.create(discussionKey.inReplyToType, discussionKey.inReplyToId),
                null).process(
                discussionService.share(
                        discussionKey.inReplyToType,
                        discussionKey.inReplyToId,
                        timelineItemShareRequestDTO));
    }

    @NotNull public MiddleCallback<DiscussionDTO> share(
            @NotNull DiscussionListKey discussionKey,
            @NotNull TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            @Nullable Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createDiscussionCreateProcessor(
                discussionKeyFactory.create(discussionKey.inReplyToType, discussionKey.inReplyToId),
                null));
        discussionServiceAsync.share(
                discussionKey.inReplyToType,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
