package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;

interface MessageServiceAsync
{
    //<editor-fold desc="Get Message Headers">
    @GET("/messages")
    void getMessageHeaders(
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<ReadablePaginatedMessageHeaderDTO> callback);

    @GET("/messages/{discussionType}/{senderId}")
    void getMessageHeaders(
            @Path("discussionType") String discussionType,
            @Path("senderId") Integer senderId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<ReadablePaginatedMessageHeaderDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Create Message">
    @POST("/messages")
    void createMessage(
            @Body MessageCreateFormDTO form,
            Callback<DiscussionDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Delete Message">
    @DELETE("/messages/delete/{commentId}/{senderUserId}/{recipientUserId}")
    void deleteMessage(
            @Path("commentId") int commentId,
            @Path("senderUserId") int senderUserId,
            @Path("recipientUserId") int recipientUserId,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Read Message">
    @POST("/messages/read/{commentId}/{senderUserId}/{recipientUserId}")
    void readMessage(
            @Path("commentId") int commentId,
            @Path("senderUserId") int senderUserId,
            @Path("recipientUserId") int recipientUserId,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Read All Message">
    @POST("/messages/read/-1")
    void readAllMessage(Callback<Response> callback);
    //</editor-fold>
}
