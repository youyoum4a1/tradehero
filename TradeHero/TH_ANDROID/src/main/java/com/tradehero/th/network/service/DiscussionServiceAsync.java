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
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by xavier on 3/7/14.
 */
public interface DiscussionServiceAsync
{
    @GET("/discussions/{commentId}")
    void getComment(
            @Path("commentId") int commentId,
            Callback<DiscussionDTO> callback);

    @Deprecated // Use getMessageThread
    @GET("/discussions/")
    void getDiscussions(
            @Query("inReplyToType") DiscussionType inReplytoType,
            @Query("inReplyToId") int inReplyToId,
            @Query("page") Integer page, // = 1
            @Query("perPage") Integer perPage,
            Callback<PaginatedDTO<DiscussionDTO>> callback); // = 42

    @GET("/discussions/{inReplyToType}/{inReplyToId}/getMessages")
    void getMessageThread(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
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

    @POST("/discussions")
    void createDiscussion(
            @Body DiscussionFormDTO discussionFormDTO,
            Callback<DiscussionDTO> callback);

    @POST("/discussions/{inReplyToType}/{inReplyToId}/vote/{direction}")
    void vote(
            @Path("inReplyToType") DiscussionType inReplytoType,
            @Path("inReplyToId") int inReplyToId,
            @Path("direction") VoteDirection direction,
            Callback<DiscussionDTO> callback);

    @POST("/discussions/{inReplyToType}/{inReplyToId}/share")
    void share(
            @Path("inReplyToType") DiscussionType inReplytoType,
            @Path("inReplyToId") int inReplyToId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            Callback<DiscussionDTO> callback);

}
