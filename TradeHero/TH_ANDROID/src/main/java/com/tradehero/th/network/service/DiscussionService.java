package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.RangedDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import java.util.Map;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by xavier on 3/7/14.
 */
public interface DiscussionService
{
    //<editor-fold desc="Get Comment">
    @GET("/discussions/{commentId}")
    DiscussionDTO getComment(@Path("commentId") int commentId);
    //</editor-fold>

    //<editor-fold desc="Get Discussions">
    @Deprecated
    @GET("/discussions/{inReplyToType}/{inReplyToId}")
    PaginatedDTO<DiscussionDTO> getDiscussions(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Query("page") Integer page, // = 1
            @Query("perPage") Integer perPage); // = 42

    @Deprecated // Use getMessageThread
    @GET("/discussions/{inReplyToType}/{inReplyToId}")
    PaginatedDTO<DiscussionDTO> getDiscussions(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @QueryMap Map<String, Object> options);
    //</editor-fold>

    //<editor-fold desc="Get Message Thread">
    @GET("/discussions/{inReplyToType}/{inReplyToId}/getMessages")
    PaginatedDTO<DiscussionDTO> getMessageThread(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Query("senderUserId") int senderUserId,
            @Query("recipientUserId") int recipientUserId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);

    @GET("/discussions/{inReplyToType}/{inReplyToId}/getMessages")
    PaginatedDTO<DiscussionDTO> getMessageThread(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @QueryMap Map<String, Object> options);
    //</editor-fold>

    //<editor-fold desc="Create Discussion">
    @POST("/discussions")
    DiscussionDTO createDiscussion(
            @Body DiscussionFormDTO discussionFormDTO);
    //</editor-fold>

    //<editor-fold desc="Vote">
    @POST("/discussions/{inReplyToType}/{inReplyToId}/vote/{direction}")
    DiscussionDTO vote(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Path("direction") VoteDirection direction);
    //</editor-fold>

    //<editor-fold desc="Share">
    @POST("/discussions/{inReplyToType}/{inReplyToId}/share")
    DiscussionDTO share(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO);
    //</editor-fold>
}
