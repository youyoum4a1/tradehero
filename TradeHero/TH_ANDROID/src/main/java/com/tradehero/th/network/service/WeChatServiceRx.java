package com.ayondo.academy.network.service;

import com.ayondo.academy.api.share.TrackShareDTO;
import com.ayondo.academy.api.share.wechat.WeChatTrackShareFormDTO;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface WeChatServiceRx
{
    @POST("/users/{userId}/trackshare")
    Observable<TrackShareDTO> trackShare(
            @Path("userId") int userId,
            @Body WeChatTrackShareFormDTO weChatTrackShareFormDTO);
}
