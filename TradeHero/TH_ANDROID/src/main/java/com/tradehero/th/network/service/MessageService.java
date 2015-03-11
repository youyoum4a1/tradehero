package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MessageService
{
    //<editor-fold desc="Get Message Headers">
    @GET("/messages") ReadablePaginatedMessageHeaderDTO getMessageHeaders(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/messages/{discussionType}/{senderId}")
    ReadablePaginatedMessageHeaderDTO getMessageHeaders(
            @Path("discussionType") String discussionType,
            @Path("senderId") Integer senderId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Message Header">
    @GET("/messages/{commentId}")
    MessageHeaderDTO getMessageHeader(
            @Path("commentId") int commentId,
            @Query("referencedUserId") Integer referencedUserId);

    @GET("/messages/thread/{correspondentId}")
    MessageHeaderDTO getMessageThread(@Path("correspondentId") int correspondentId);
    //</editor-fold>

    //<editor-fold desc="Get Messaging Relationship Status">
    @GET("/messages/status/{recipientUserId}")
    UserMessagingRelationshipDTO getMessagingRelationgshipStatus(
            @Path("recipientUserId") int recipientUserId);
    //</editor-fold>

    //<editor-fold desc="Read All Message">
    @POST("/messages/read/-1")
    Response readAllMessage();
    //</editor-fold>
}
