package com.tradehero.th.network.service;

import com.tradehero.th.api.share.wechat.WeChatTrackShareFormDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class WeChatServiceWrapper
{
    @NotNull private final WeChatServiceAsync weChatServiceAsync;

    @Inject public WeChatServiceWrapper(
            @NotNull WeChatServiceAsync weChatServiceAsync)
    {
        super();
        this.weChatServiceAsync = weChatServiceAsync;
    }

    public BaseMiddleCallback<Response> trackShare(int userId, WeChatTrackShareFormDTO weChatTrackShareFormDTO,
            Callback<Response> callback)
    {
        BaseMiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        weChatServiceAsync.trackShare(userId, weChatTrackShareFormDTO, middleCallback);
        return middleCallback;
    }
}
