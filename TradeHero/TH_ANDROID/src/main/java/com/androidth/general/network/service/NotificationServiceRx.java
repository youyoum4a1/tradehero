package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.notification.NotificationDTO;
import com.androidth.general.api.notification.PaginatedNotificationDTO;
import java.util.Map;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
//import retrofit2.http.QueryMap;
import retrofit2.http.QueryMap;
import rx.Observable;

interface NotificationServiceRx
{
    @GET("api/notifications")
    Observable<PaginatedNotificationDTO> getNotifications(@QueryMap Map<String, Object> options);

    @GET("api/notifications/{pushId}")
    Observable<NotificationDTO> getNotificationDetail(@Path("pushId") int pushId);

    @POST("api/notifications/read/{readPushId}")
    Observable<BaseResponseDTO> markAsRead(@Path("readPushId") int readPushId);

    @POST("api/notifications/read/-1")
    Observable<BaseResponseDTO> markAsReadAll();
}
