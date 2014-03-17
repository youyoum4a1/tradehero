package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by xavier on 3/7/14.
 */
interface DiscussionServiceAsync
{
    @POST("/discussions")
    void createDiscussion(
            @Body DiscussionDTO discussionDTO,
            Callback<DiscussionDTO> callback);

    @POST("/discussions/{inReplyToType}/{inReplyToId}/vote/{direction}")
    void vote(
            @Path("inReplyToType") String inReplyToType,
            @Path("inReplyToId") int inReplyToId,
            @Path("direction") String direction,
            Callback<DiscussionDTO> callback);
}
