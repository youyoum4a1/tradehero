package com.tradehero.th.network.service;

import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by xavier2 on 2014/4/9.
 */
interface MessageServiceAsync
{
    @GET("/messages")
    void getMessages(
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<PaginatedDTO<MessageDTO>> callback);

    @POST("/messages")
    void createMessage(
            @Body MessageDTO form,
            Callback<DiscussionDTO> callback);
}
