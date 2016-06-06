package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.MessageHeaderDTO;
import com.androidth.general.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.androidth.general.api.discussion.form.MessageCreateFormDTO;
import com.androidth.general.api.users.UserMessagingRelationshipDTO;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface MessageServiceRx
{
    //<editor-fold desc="Get Message Headers">
    @GET("/messages")
    Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeaders(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/messages/{discussionType}/{senderId}")
    Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeaders(
            @Path("discussionType") String discussionType,
            @Path("senderId") Integer senderId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Message Header">
    @GET("/messages/{commentId}")
    Observable<MessageHeaderDTO> getMessageHeader(
            @Path("commentId") int commentId,
            @Query("referencedUserId") Integer referencedUserId);

    @GET("/messages/thread/{correspondentId}")
    Observable<MessageHeaderDTO> getMessageThread(@Path("correspondentId") int correspondentId);
    //</editor-fold>

    //<editor-fold desc="Get Messaging Relationship Status">
    @GET("/messages/status/{recipientUserId}")
    Observable<UserMessagingRelationshipDTO> getMessagingRelationgshipStatus(
            @Path("recipientUserId") int recipientUserId);
    //</editor-fold>

    //<editor-fold desc="Create Message">
    @POST("/messages")
    Observable<DiscussionDTO> createMessage(@Body MessageCreateFormDTO form);
    //</editor-fold>

    //<editor-fold desc="Delete Message">
    @DELETE("/messages/delete/{commentId}/{senderUserId}/{recipientUserId}")
    Observable<BaseResponseDTO> deleteMessage(
            @Path("commentId") int commentId,
            @Path("senderUserId") int senderUserId,
            @Path("recipientUserId") int recipientUserId);
    //</editor-fold>

    //<editor-fold desc="Read Message">
    @POST("/messages/read/{commentId}/{senderUserId}/{recipientUserId}")
    Observable<BaseResponseDTO> readMessage(
            @Path("commentId") int commentId,
            @Path("senderUserId") int senderUserId,
            @Path("recipientUserId") int recipientUserId);
    //</editor-fold>

    //<editor-fold desc="Read All Message">
    @POST("/messages/read/-1")
    Observable<BaseResponseDTO> readAllMessage();
    //</editor-fold>
}
