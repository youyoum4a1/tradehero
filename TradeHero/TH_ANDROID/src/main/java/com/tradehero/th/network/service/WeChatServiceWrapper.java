package com.tradehero.th.network.service;

import com.tradehero.th.api.wechat.TrackShareFormDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.client.Response;


@Singleton public class WeChatServiceWrapper
{
    private final WeChatService weChatService;

    @Inject public WeChatServiceWrapper(WeChatService weChatService)
    {
        super();
        this.weChatService = weChatService;
    }

    public BaseMiddleCallback<Response> trackShare(int userId, TrackShareFormDTO trackShareFormDTO,
            Callback<Response> callback)
    {
        BaseMiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        weChatService.trackShare(userId, trackShareFormDTO, callback);
        return middleCallback;
    }
}
