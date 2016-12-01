package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.VoteDirection;
import com.androidth.general.api.discussion.form.DiscussionFormDTO;
import com.androidth.general.api.pagination.PaginatedDTO;
import com.androidth.general.api.timeline.TimelineItemShareRequestDTO;
import java.util.Map;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

interface DiscussionServiceRx
{
    //<editor-fold desc="Get Comment">
    @GET("api/discussions/{commentId}")
    Observable<DiscussionDTO> getComment(@Path("commentId") int commentId);
    //</editor-fold>

    //<editor-fold desc="Get Discussions">
    @Deprecated
    @GET("api/discussions/{inReplyToType}/{inReplyToId}")
    Observable<PaginatedDTO<DiscussionDTO>> getDiscussions(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Query("page") Integer page, // = 1
            @Query("perPage") Integer perPage); // = 42
    //</editor-fold>

    //<editor-fold desc="Get Message Thread">
    @GET("api/discussions/{inReplyToType}/{inReplyToId}/getMessages")
    Observable<PaginatedDTO<DiscussionDTO>> getMessageThread(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Query("senderUserId") int senderUserId,
            @Query("recipientUserId") int recipientUserId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);

    @GET("api/discussions/{inReplyToType}/{inReplyToId}/getMessages")
    Observable<PaginatedDTO<DiscussionDTO>> getMessageThread(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @QueryMap Map<String, Object> options);
    //</editor-fold>

    //<editor-fold desc="Create Discussion">
    @POST("api/discussions")
    Observable<DiscussionDTO> createDiscussion(
            @Body DiscussionFormDTO discussionFormDTO);
    //</editor-fold>

    //<editor-fold desc="Vote">
    @POST("api/discussions/{inReplyToType}/{inReplyToId}/vote/{direction}")
    Observable<DiscussionDTO> vote(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Path("direction") VoteDirection direction);
    //</editor-fold>

    //<editor-fold desc="Share">
    @POST("api/discussions/{inReplyToType}/{inReplyToId}/share")
    Observable<BaseResponseDTO> share(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO);
    //</editor-fold>

    //<editor-fold desc="Post to Timeline">
    @POST("api/users/{userId}/timeline")
    Observable<DiscussionDTO> postToTimeline(
            @Path("userId") int userId,
            @Body DiscussionFormDTO discussionFormDTO);
    //</editor-fold>
}
