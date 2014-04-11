package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
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
            Callback<PaginatedDTO<MessageHeaderDTO>> callback);

    // TODO implement this on server
    @GET("/messages/{discussionType}")
    void getMessages(
            @Path("discussionType") DiscussionType discussionType,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("recipientId") Integer recipientId,
            Callback<PaginatedDTO<MessageHeaderDTO>> callback);

    @GET("/messages/{commentId}")
    void getMessageHeader(
            @Path("commentId") int commentId,
            Callback<MessageHeaderDTO> callback);

    @GET("/messages/{recipientUserId}/getFreeCount")
    void getFreeCount(
            @Path("recipientUserId") int recipientUserId,
            Callback<MessageStatusDTO> callback);

    @POST("/messages")
    void createMessage(
            @Body MessageHeaderDTO form,
            Callback<DiscussionDTO> callback);


    //TODO fake
    @POST("/messages")
    void deleteMessage(
            @Body MessageHeaderDTO form,
            Callback<DiscussionDTO> callback);
}
