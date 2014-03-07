package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by xavier on 3/7/14.
 */
interface DiscussionServiceAsync
{
    @POST("/discussions")
    void createDiscussion(
            @Body DiscussionDTO discussionDTO,
            Callback<DiscussionDTO> callback);

    // TODO add methods async based on DiscussionService
}
