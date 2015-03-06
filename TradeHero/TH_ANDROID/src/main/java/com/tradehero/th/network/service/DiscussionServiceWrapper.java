package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.ApplyCommentDTO;
import com.tradehero.chinabuild.data.DiscoveryDiscussFormDTO;
import com.tradehero.chinabuild.data.DiscussReportDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.key.*;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.discussion.DTOProcessorDiscussion;
import com.tradehero.th.models.discussion.DTOProcessorDiscussionCreate;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class DiscussionServiceWrapper
{
    @NotNull private final DiscussionService discussionService;
    @NotNull private final DiscussionServiceAsync discussionServiceAsync;
    @NotNull private final DiscussionDTOFactory discussionDTOFactory;

    // It has to be lazy to avoid infinite dependency
    @NotNull private final Lazy<DiscussionCache> discussionCache;
    @NotNull private final Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionServiceWrapper(
            @NotNull DiscussionService discussionService,
            @NotNull DiscussionServiceAsync discussionServiceAsync,
            @NotNull DiscussionDTOFactory discussionDTOFactory,
            @NotNull Lazy<DiscussionCache> discussionCache,
            @NotNull Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache)
    {
        this.discussionService = discussionService;
        this.discussionServiceAsync = discussionServiceAsync;
        this.discussionDTOFactory = discussionDTOFactory;
        this.discussionCache = discussionCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
    }
    //</editor-fold>

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
                userMessagingRelationshipCache.get(),
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

    public MiddleCallback<TimelineItemDTO> createDiscoveryDiscussion(
            int userId,
            DiscoveryDiscussFormDTO discussionFormDTO,
            Callback<TimelineItemDTO> callback)
    {
        MiddleCallback<TimelineItemDTO> middleCallback = new BaseMiddleCallback<>(callback);
        discussionServiceAsync.createDiscoveryDiscussion(userId, discussionFormDTO, middleCallback);
        return middleCallback;
    }

    public void createRewardTimeLine(int userId,
                                     DiscoveryDiscussFormDTO discussionFormDTO,
                                     Callback<Response> callback){
        discussionServiceAsync.createRewaredTimeLine(userId, discussionFormDTO, callback);
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

    public PaginatedDTO<DiscussionDTO> getMessageThread(MessageDiscussionListKey discussionsKey)
    {
        return discussionService.getMessageThread(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.toMap());
    }

    public MiddleCallback<DiscussionDTO> vote(DiscussionVoteKey discussionVoteKey, Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
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


    public void reportTimeLineItem(DiscussReportDTO discussReportDTO, Callback<Response> callback){
        discussionServiceAsync.reportTimeLineItem(discussReportDTO, callback);
    }

    public void deleteTimeLineItem(int timelineItemId, Callback<Response> callback){
        discussionServiceAsync.deleteTimeLineItem(timelineItemId, callback);
    }

    public void deleteDiscussionItem(int discussionItemId, Callback<Response> callback){
        discussionServiceAsync.deleteDiscussionItem(discussionItemId, callback);
    }

    public void applyRewardTimeLineAnswer(int userId, int timeLineItemId, int commentId, Callback<Response> callback){
        ApplyCommentDTO applyCommentDTO = new ApplyCommentDTO();
        applyCommentDTO.commentId = commentId;
        discussionServiceAsync.applyRewardTimeLineAnswer(userId, timeLineItemId, applyCommentDTO, callback);
    }


}
