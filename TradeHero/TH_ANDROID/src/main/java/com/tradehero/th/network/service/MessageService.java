package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import retrofit.http.Body;
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

    @GET("/messages")
    PaginatedDTO<MessageHeaderDTO> getMessages(
            @Query("discussType") DiscussionType discussionType,
            @Query("senderId") Integer senderId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/messages/{commentId}")
    MessageHeaderDTO getMessageHeader(@Path("commentId") int commentId);

    @GET("/messages/{recipientUserId}/getFreeCount")
    MessageStatusDTO getFreeCount(@Path("recipientUserId") int recipientUserId);

    @POST("/messages")
    DiscussionDTO createMessage(@Body MessageHeaderDTO form);
}
