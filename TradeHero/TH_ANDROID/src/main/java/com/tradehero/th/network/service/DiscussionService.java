package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
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
    @GET("/discussions/{commentId}")
    DiscussionDTO getComment(@Path("commentId") int commentId);

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

    @GET("/discussions/{inReplyToType}/{inReplyToId}/getMessages")
    RangedDTO<AbstractDiscussionDTO, DiscussionDTOList> getMessageThread(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);

    @GET("/discussions/{inReplyToType}/{inReplyToId}/getMessages")
    RangedDTO<AbstractDiscussionDTO, DiscussionDTOList> getMessageThread(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @QueryMap Map<String, Object> options);

    @POST("/discussions")
    DiscussionDTO createDiscussion(
            @Body DiscussionDTO discussionDTO);

    @POST("/discussions/{inReplyToType}/{inReplyToId}/vote/{direction}")
    DiscussionDTO vote(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Path("direction") VoteDirection direction);

    @POST("/discussions/{inReplyToType}/{inReplyToId}/share")
    DiscussionDTO share(
            @Path("inReplyToType") DiscussionType inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO);
}
