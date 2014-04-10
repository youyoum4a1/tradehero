package com.tradehero.th.network.service;

import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface MessageService
{
    @GET("/messages")
    PaginatedDTO<MessageHeaderDTO> getMessages(
            @Query("page") int page,
            @Query("perPage") int perPage);

    // TODO can we have a single message GET function?
    //@GET("/messages/{msgId}") MessageDetailDTO getMessageDetail(@Path("msgId") int msgId);

    @POST("/messages")
    DiscussionDTO createMessage(@Body MessageHeaderDTO form);
}
