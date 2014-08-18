package com.tradehero.th.network.service;

import com.tradehero.th.api.share.wechat.WeChatTrackShareFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.client.Response;

@Singleton public class WeChatServiceWrapper
{
    @NotNull private final WeChatService weChatService;
    @NotNull private final WeChatServiceAsync weChatServiceAsync;

    @Inject public WeChatServiceWrapper(
            @NotNull WeChatService weChatService,
            @NotNull WeChatServiceAsync weChatServiceAsync)
    {
        super();
        this.weChatService = weChatService;
        this.weChatServiceAsync = weChatServiceAsync;
    }

    public Response trackShare(UserBaseKey userId, WeChatTrackShareFormDTO weChatTrackShareFormDTO)
    {
        return weChatService.trackShare(userId.key, weChatTrackShareFormDTO);
    }

    public BaseMiddleCallback<Response> trackShare(UserBaseKey userId, WeChatTrackShareFormDTO weChatTrackShareFormDTO,
            Callback<Response> callback)
    {
        BaseMiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        weChatServiceAsync.trackShare(userId.key, weChatTrackShareFormDTO, middleCallback);
        return middleCallback;
    }
}
