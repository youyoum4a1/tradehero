package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.MessageHeaderDTO;
import com.androidth.general.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.androidth.general.api.discussion.form.MessageCreateFormDTO;
import com.androidth.general.api.users.UserMessagingRelationshipDTO;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface MessageServiceRx
{
    //<editor-fold desc="Get Message Headers">
    @GET("api/messages")
    Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeaders(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/messages/{discussionType}/{senderId}")
    Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeaders(
            @Path("discussionType") String discussionType,
            @Path("senderId") Integer senderId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Message Header">
    @GET("api/messages/{commentId}")
    Observable<MessageHeaderDTO> getMessageHeader(
            @Path("commentId") int commentId,
            @Query("referencedUserId") Integer referencedUserId);

    @GET("api/messages/thread/{correspondentId}")
    Observable<MessageHeaderDTO> getMessageThread(@Path("correspondentId") int correspondentId);
    //</editor-fold>

    //<editor-fold desc="Get Messaging Relationship Status">
    @GET("api/messages/status/{recipientUserId}")
    Observable<UserMessagingRelationshipDTO> getMessagingRelationgshipStatus(
            @Path("recipientUserId") int recipientUserId);
    //</editor-fold>

    //<editor-fold desc="Create Message">
    @POST("api/messages")
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
    @POST("api/messages/read/{commentId}/{senderUserId}/{recipientUserId}")
    Observable<BaseResponseDTO> readMessage(
            @Path("commentId") int commentId,
            @Path("senderUserId") int senderUserId,
            @Path("recipientUserId") int recipientUserId);
    //</editor-fold>

    //<editor-fold desc="Read All Message">
    @POST("api/messages/read/-1")
    Observable<BaseResponseDTO> readAllMessage();
    //</editor-fold>
}
