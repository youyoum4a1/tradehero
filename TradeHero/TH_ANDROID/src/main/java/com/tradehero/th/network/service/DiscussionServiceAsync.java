package com.tradehero.th.network.service;

import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import retrofit.Callback;
import retrofit.http.*;

/**
 * Created by xavier on 3/7/14.
 */
public interface DiscussionServiceAsync
{
    @POST("/discussions")
    void createDiscussion(
            @Body DiscussionDTO discussionDTO,
            Callback<DiscussionDTO> callback);

    // TODO add methods async based on DiscussionService

    @GET("/discussions/")
    void getDiscussions(
            @Query("inReplyToType") String inReplytoType,
            @Query("inReplyToId") int inReplyToId,
            @Query("page") Integer page, // = 1
            @Query("perPage") Integer perPage,
            Callback<PaginatedDTO<DiscussionDTO>> callback); // = 42


    @POST("/discussions/{inReplyToType}/{inReplyToId}/vote/{direction}")
    void vote(
            @Path("inReplyToType") String inReplytoType,
            @Path("inReplyToId") int inReplyToId,
            @Path("direction") String direction,
            Callback<DiscussionDTO> callback);

    @POST("/discussions/{inReplyToType}/{inReplyToId}/share")
    void share(
            @Path("inReplyToType") String inReplytoType,
            @Path("inReplyToId") int inReplyToId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            Callback<DiscussionDTO> callback);
}
