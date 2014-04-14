package com.tradehero.th.network.service;

import com.tradehero.th.api.wechat.TrackShareFormDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by alex on 14-4-4.
 */
@Singleton public class WeChatServiceWrapper
{
    private final WeChatService weChatService;

    @Inject public WeChatServiceWrapper(WeChatService weChatService)
    {
        super();
        this.weChatService = weChatService;
    }

    public MiddleCallback<Response> trackShare(int userId, TrackShareFormDTO trackShareFormDTO,
            Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new MiddleCallback<>(callback);
        weChatService.trackShare(userId, trackShareFormDTO, callback);
        return middleCallback;
    }
}
