package com.androidth.general.network.service;

import com.androidth.general.api.share.TrackShareDTO;
import com.androidth.general.api.share.wechat.WeChatTrackShareFormDTO;
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
