package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.ApplyCommentDTO;
import com.tradehero.chinabuild.data.DiscoveryDiscussFormDTO;
import com.tradehero.chinabuild.data.DiscussReportDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.Map;

interface DiscussionServiceAsync
{
    //<editor-fold desc="Get Comment">
    @GET("/discussions/{commentId}")
    void getComment(
            @Path("commentId") int commentId,
            Callback<DiscussionDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Discussions">
    @Deprecated // Use getMessageThread
    @GET("/discussions/")
    void getDiscussions(
            @Query("inReplyToType") DiscussionType inReplytoType,
            @Query("inReplyToId") int inReplyToId,
            @Query("page") Integer page, // = 1
            @Query("perPage") Integer perPage,
            Callback<PaginatedDTO<DiscussionDTO>> callback); // = 42
    //</editor-fold>

    //<editor-fold desc="Get Message Thread">
    @GET("/discussions/{inReplyToType}/{inReplyToId}/getMessages")
    void getMessageThread(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Query("senderUserId") int senderUserId,
            @Query("recipientUserId") int recipientUserId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId,
            Callback<PaginatedDTO<DiscussionDTO>> callback);

    @GET("/discussions/{inReplyToType}/{inReplyToId}/getMessages")
    void getMessageThread(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @QueryMap Map<String, Object> options,
            Callback<PaginatedDTO<DiscussionDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Create Discussion">
    @POST("/discussions")
    void createDiscussion(
            @Body DiscussionFormDTO discussionFormDTO,
            Callback<DiscussionDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Vote">
    @POST("/discussions/{inReplyToType}/{inReplyToId}/vote/{direction}")
    void vote(
            @Path("inReplyToType") DiscussionType inReplytoType,
            @Path("inReplyToId") int inReplyToId,
            @Path("direction") VoteDirection direction,
            Callback<DiscussionDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Share">
    @POST("/discussions/{inReplyToType}/{inReplyToId}/share")
    void share(
            @Path("inReplyToType") DiscussionType inReplytoType,
            @Path("inReplyToId") int inReplyToId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            Callback<DiscussionDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Create Discussion">
    @POST("/users/{userId}/timeline")
    void createDiscoveryDiscussion(
            @Path("userId") int userId,
            @Body DiscoveryDiscussFormDTO discussionFormDTO,
            Callback<TimelineItemDTO> callback);

    @POST("/users/{userId}/timeline")
    void createRewaredTimeLine(
            @Path("userId") int userId,
            @Body DiscoveryDiscussFormDTO discussionFormDTO,
            Callback<Response> callback);
    //</editor-fold>

    @POST("/report")
    void reportTimeLineItem(@Body DiscussReportDTO discussReportDTO, Callback<Response> callback);

    @DELETE("/timeline/{timelineItemId}")
    void deleteTimeLineItem(@Path("timelineItemId")int timelineItemId, Callback<Response> callback);

    @DELETE("/discussions/{discussionId}")
    void deleteDiscussionItem(@Path("discussionId")int discussionId, Callback<Response> callback);

    @POST("/users/{userid}/timeline/{timelineid}/pickAnswer")
    void applyRewardTimeLineAnswer(@Path("userid")int userid,@Path("timelineid")int timelineid,@Body ApplyCommentDTO applyCommentDTO,Callback<Response> callback);
}
