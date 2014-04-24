package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

interface MessageServiceAsync
{
    //<editor-fold desc="Get Message Headers">
    @GET("/messages")
    void getMessageHeaders(
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<PaginatedDTO<MessageHeaderDTO>> callback);

    @GET("/messages/{discussionType}/{senderId}")
    void getMessageHeaders(
            @Path("discussionType") String discussionType,
            @Path("senderId") Integer senderId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<PaginatedDTO<MessageHeaderDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Message Header">
    @GET("/messages/{commentId}")
    void getMessageHeader(
            @Path("commentId") int commentId,
            Callback<MessageHeaderDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Messaging Relationship Status">
    @GET("/messages/status/{recipientUserId}")
    void getMessagingRelationshipStatus(
            @Path("recipientUserId") int recipientUserId,
            Callback<UserMessagingRelationshipDTO> callback);
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
}
