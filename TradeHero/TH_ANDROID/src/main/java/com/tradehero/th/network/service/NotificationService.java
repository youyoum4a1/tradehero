package com.tradehero.th.network.service;

import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import java.util.Map;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by thonguyen on 3/4/14.
 */
public interface NotificationService
{
    @GET("/notifications")
    PaginatedDTO<NotificationDTO> getNotifications(@QueryMap Map<String, Object> options);

    @GET("/notifications/{pushId}")
    NotificationDTO getNotificationDetail(@Path("pushId") int pushId);

    @POST("/notifications/read/{readPushId}")
    Response markAsRead(@Path("readPushId") int readPushId);
}
