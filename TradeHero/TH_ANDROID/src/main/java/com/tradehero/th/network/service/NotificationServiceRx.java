package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import java.util.Map;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

interface NotificationServiceRx
{
    @GET("/notifications")
    Observable<PaginatedNotificationDTO> getNotifications(@QueryMap Map<String, Object> options);

    @GET("/notifications/{pushId}")
    Observable<NotificationDTO> getNotificationDetail(@Path("pushId") int pushId);

    @POST("/notifications/read/{readPushId}")
    Observable<BaseResponseDTO> markAsRead(@Path("readPushId") int readPushId);

    @POST("/notifications/read/-1")
    Observable<BaseResponseDTO> markAsReadAll();
}
