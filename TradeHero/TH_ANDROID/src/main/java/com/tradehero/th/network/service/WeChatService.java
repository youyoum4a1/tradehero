package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.share.wechat.WeChatTrackShareFormDTO;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface WeChatService
{
    @POST("/users/{userId}/trackshare")
    BaseResponseDTO trackShare(
            @Path("userId") int userId,
            @Body WeChatTrackShareFormDTO weChatTrackShareFormDTO);
}
