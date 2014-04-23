package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MessageService
{
    //<editor-fold desc="Get Message Headers">
    @GET("/messages")
    PaginatedDTO<MessageHeaderDTO> getMessageHeaders(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/messages/{discussionType}/{senderId}")
    PaginatedDTO<MessageHeaderDTO> getMessageHeaders(
            @Path("discussionType") String discussionType,
            @Path("senderId") Integer senderId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Message Header">
    @GET("/messages/{commentId}")
    MessageHeaderDTO getMessageHeader(@Path("commentId") int commentId);
    //</editor-fold>

    //<editor-fold desc="Get Messaging Relationship Status">
    @GET("/messages/status/{recipientUserId}")
    UserMessagingRelationshipDTO getMessagingRelationgshipStatus(
            @Path("recipientUserId") int recipientUserId);
    //</editor-fold>

    //<editor-fold desc="Create Message">
    @POST("/messages")
    DiscussionDTO createMessage(@Body MessageCreateFormDTO form);
    //</editor-fold>

    //<editor-fold desc="Delete Message">
    @DELETE("/messages/{commentId}")
    Response deleteMessage(@Path("commentId") int commentId);
    //</editor-fold>

    //<editor-fold desc="Read Message">
    @POST("/messages/read/{commentId}/{senderUserId}/{recipientUserId}")
    Response readMessage(
            @Path("commentId") int commentId,
            @Path("senderUserId") int senderUserId,
            @Path("recipientUserId") int recipientUserId);
    //</editor-fold>
}
