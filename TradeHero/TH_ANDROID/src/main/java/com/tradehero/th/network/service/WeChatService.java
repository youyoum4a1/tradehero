package com.tradehero.th.network.service;

import com.tradehero.th.api.wechat.TrackShareFormDTO;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface WeChatService
{
    @POST("/users/{userId}/trackshare")
    Response trackShare(
            @Path("userId") int userId,
            @Body TrackShareFormDTO trackShareFormDTO);
}
