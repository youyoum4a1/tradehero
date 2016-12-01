package com.androidth.general.network.service;

import com.androidth.general.api.share.TrackShareDTO;
import com.androidth.general.api.share.wechat.WeChatTrackShareFormDTO;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface WeChatServiceRx
{
    @POST("api/users/{userId}/trackshare")
    Observable<TrackShareDTO> trackShare(
            @Path("userId") int userId,
            @Body WeChatTrackShareFormDTO weChatTrackShareFormDTO);
}
