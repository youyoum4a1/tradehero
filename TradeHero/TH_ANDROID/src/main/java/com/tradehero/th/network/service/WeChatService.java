package com.tradehero.th.network.service;

import com.tradehero.th.api.wechat.TrackShareFormDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by alex on 14-4-4.
 */
public interface WeChatService
{
    @POST("/users/{userId}/trackshare")
    void trackShare(
            @Path("userId") int userId,
            @Body TrackShareFormDTO trackShareFormDTO,
            Callback<Response> callback);
}
