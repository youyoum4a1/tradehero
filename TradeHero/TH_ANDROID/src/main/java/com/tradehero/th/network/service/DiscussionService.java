package com.tradehero.th.network.service;

import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by xavier on 3/7/14.
 */
public interface DiscussionService
{
    @GET("/discussions/")
    PaginatedDTO<DiscussionDTO> getDiscussions(
            @Query("inReplytoType") String inReplytoType,
            @Query("inReplyToId") int inReplyToId,
            @Query("page") Integer page, // = 1
            @Query("perPage") Integer perPage); // = 42

    @POST("/discussions")
    DiscussionDTO createDiscussion(
            @Body DiscussionDTO discussionDTO);

    @POST("/discussions/{inReplyToType}/{inReplyToId}/vote/{direction}")
    DiscussionDTO vote(
            @Path("inReplytoType") String inReplytoType,
            @Path("inReplyToId") int inReplyToId,
            @Path("direction") String direction);

    @POST("/discussions/{inReplyToType}/{inReplyToId}/share")
    DiscussionDTO share(
            @Path("inReplytoType") String inReplytoType,
            @Path("inReplyToId") int inReplyToId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO);
}
