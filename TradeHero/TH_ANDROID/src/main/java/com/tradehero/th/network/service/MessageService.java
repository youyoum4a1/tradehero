package com.tradehero.th.network.service;

import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.messages.MessageDTO;
import com.tradehero.th.api.messages.MessageDetailDTO;
import com.tradehero.th.api.notification.NotificationDTO;
import java.util.Map;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 *
 */
public interface MessageService
{
    @GET("/messages") PaginatedDTO<MessageDTO> getMessages(@QueryMap Map<String, Object> options);

    @GET("/messages/{msgId}") MessageDetailDTO getMessageDetail(@Path("msgId") int msgId);

    @GET("/messages/CreateMessage") void createMessage(@Field("message")String message,
    @Field("senderUserId")int userId
    );

    @GET("/messages/CreateMessage") void createMessage(@Field("message")String message,
            @Field("senderUserId")int userId, Callback<Response> callback
    );
}
