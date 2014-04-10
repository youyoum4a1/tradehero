package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import sun.net.www.MessageHeader;

public interface MessageService
{
    @GET("/messages")
    PaginatedDTO<MessageHeaderDTO> getMessages(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    // TODO implement this on server
    @GET("/messages/{discussionType}")
    PaginatedDTO<MessageHeaderDTO> getMessages(
            @Path("discussionType") DiscussionType discussionType,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("recipientId") Integer recipientId);

    @GET("/messages/{commentId}")
    MessageHeaderDTO getMessageHeader(@Path("commentId") int commentId);

    @POST("/messages")
    DiscussionDTO createMessage(@Body MessageHeaderDTO form);
}
