package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.share.wechat.WeChatTrackShareFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class WeChatServiceWrapper
{
    @NotNull private final WeChatService weChatService;
    @NotNull private final WeChatServiceAsync weChatServiceAsync;

    //<editor-fold desc="Constructors">
    @Inject public WeChatServiceWrapper(
            @NotNull WeChatService weChatService,
            @NotNull WeChatServiceAsync weChatServiceAsync)
    {
        super();
        this.weChatService = weChatService;
        this.weChatServiceAsync = weChatServiceAsync;
    }
    //</editor-fold>

    //<editor-fold desc="Track Share">
    public BaseResponseDTO trackShare(
            @NotNull UserBaseKey userId,
            @NotNull WeChatTrackShareFormDTO weChatTrackShareFormDTO)
    {
        return weChatService.trackShare(userId.key, weChatTrackShareFormDTO);
    }

    public BaseMiddleCallback<BaseResponseDTO> trackShare(
            @NotNull UserBaseKey userId,
            @NotNull WeChatTrackShareFormDTO weChatTrackShareFormDTO,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        BaseMiddleCallback<BaseResponseDTO> middleCallback = new BaseMiddleCallback<>(callback);
        weChatServiceAsync.trackShare(userId.key, weChatTrackShareFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
