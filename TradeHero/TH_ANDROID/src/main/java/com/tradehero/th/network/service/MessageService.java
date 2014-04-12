package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MessageService
{
    @GET("/messages")
    PaginatedDTO<MessageHeaderDTO> getMessages(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/messages/{discussionType}/{senderId}")
    PaginatedDTO<MessageHeaderDTO> getMessages(
            @Path("discussionType") String discussionType,
            @Path("senderId") Integer senderId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/messages/{commentId}")
    MessageHeaderDTO getMessageHeader(@Path("commentId") int commentId);

    @GET("/messages/{recipientUserId}")
    //@GET("/messages/status/{recipientUserId}") // Proper way to activate when deployed
    MessageStatusDTO getStatus(@Path("recipientUserId") int recipientUserId);

    @POST("/messages")
    DiscussionDTO createMessage(@Body MessageCreateFormDTO form);

    @DELETE("/messages/{commentId}")
    Response deleteMessage(@Path("commentId") int commentId);

    @POST("/messages/read/{commentId}")
    Response readMessage(@Path("commentId") int commentId);
}
