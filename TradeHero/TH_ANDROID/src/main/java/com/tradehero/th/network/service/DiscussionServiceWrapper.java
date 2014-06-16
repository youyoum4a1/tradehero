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
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.discussion.DTOProcessorDiscussion;
import com.tradehero.th.models.discussion.DTOProcessorDiscussionCreate;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

@Singleton public class DiscussionServiceWrapper
{
    @NotNull private final DiscussionService discussionService;
    @NotNull private final DiscussionServiceAsync discussionServiceAsync;
    @NotNull private final DiscussionDTOFactory discussionDTOFactory;

    // It has to be lazy to avoid infinite dependency
    @NotNull private final Lazy<DiscussionCache> discussionCache;

    @Inject public DiscussionServiceWrapper(
            @NotNull DiscussionService discussionService,
            @NotNull DiscussionServiceAsync discussionServiceAsync,
            @NotNull DiscussionDTOFactory discussionDTOFactory,
            @NotNull Lazy<DiscussionCache> discussionCache)
    {
        this.discussionService = discussionService;
        this.discussionServiceAsync = discussionServiceAsync;
        this.discussionDTOFactory = discussionDTOFactory;
        this.discussionCache = discussionCache;
    }

    //<editor-fold desc="DTO Processors">
    protected DTOProcessor<DiscussionDTO> createDiscussionProcessor()
    {
        return new DTOProcessorDiscussion(discussionDTOFactory);
    }

    protected DTOProcessor<DiscussionDTO> createDiscussionCreateProcessor(DiscussionKey stubKey)
    {
        return new DTOProcessorDiscussionCreate(
                discussionDTOFactory,
                discussionCache.get(),
                stubKey);
    }
    //</editor-fold>

    // TODO add providers in RetrofitModule and RetrofitProtectedModule
    // TODO add methods based on DiscussionServiceAsync and MiddleCallback implementations

    //<editor-fold desc="Get Comment">
    public DiscussionDTO getComment(DiscussionKey discussionKey)
    {
        return createDiscussionProcessor().process(discussionService.getComment(discussionKey.id));
    }

    public MiddleCallback<DiscussionDTO> getComment(DiscussionKey discussionKey, Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createDiscussionProcessor());
        discussionServiceAsync.getComment(discussionKey.id, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Create Discussion">
    public DiscussionDTO createDiscussion(DiscussionFormDTO discussionFormDTO)
    {
        return createDiscussionCreateProcessor(discussionFormDTO.stubKey).process(
            discussionService.createDiscussion(discussionFormDTO));
    }

    public MiddleCallback<DiscussionDTO> createDiscussion(
            DiscussionFormDTO discussionFormDTO,
            Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createDiscussionCreateProcessor(discussionFormDTO.stubKey));
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

    public MiddleCallback<PaginatedDTO<DiscussionDTO>> getMessageThread(
            MessageDiscussionListKey discussionsKey,
            Callback<PaginatedDTO<DiscussionDTO>> callback)
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
    public DiscussionDTO vote(DiscussionVoteKey discussionVoteKey)
    {
        return createDiscussionCreateProcessor(null).process(discussionService.vote(
                discussionVoteKey.inReplyToType,
                discussionVoteKey.inReplyToId,
                discussionVoteKey.voteDirection));
    }

    public MiddleCallback<DiscussionDTO> vote(DiscussionVoteKey discussionVoteKey, Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback =  new BaseMiddleCallback<>(
                callback, createDiscussionCreateProcessor(null));
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
        return createDiscussionCreateProcessor(null).process(
            discussionService.share(
                discussionKey.inReplyToType,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO));
    }

    public MiddleCallback<DiscussionDTO> share(
            DiscussionListKey discussionKey,
            TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createDiscussionCreateProcessor(null));
        discussionServiceAsync.share(
                discussionKey.inReplyToType,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
