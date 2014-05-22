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
    private final WeChatServiceAsync weChatServiceAsync;

    @Inject public WeChatServiceWrapper(
            WeChatService weChatService,
            WeChatServiceAsync weChatServiceAsync)
    {
        super();
        this.weChatService = weChatService;
        this.weChatServiceAsync = weChatServiceAsync;
    }

    public Response trackShare(int userId, TrackShareFormDTO trackShareFormDTO)
    {
        return weChatService.trackShare(userId, trackShareFormDTO);
    }

    public BaseMiddleCallback<Response> trackShare(int userId, TrackShareFormDTO trackShareFormDTO,
            Callback<Response> callback)
    {
        BaseMiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        weChatServiceAsync.trackShare(userId, trackShareFormDTO, middleCallback);
        return middleCallback;
    }
}
