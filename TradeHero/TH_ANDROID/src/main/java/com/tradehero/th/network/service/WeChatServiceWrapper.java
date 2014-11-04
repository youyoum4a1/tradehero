package com.tradehero.th.network.service;

import com.tradehero.th.api.share.TrackShareDTO;
import com.tradehero.th.api.share.wechat.WeChatTrackShareFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class WeChatServiceWrapper
{
    @NotNull private final WeChatServiceRx weChatServiceRx;
    @NotNull private final WeChatServiceAsync weChatServiceAsync;

    //<editor-fold desc="Constructors">
    @Inject public WeChatServiceWrapper(
            @NotNull WeChatServiceAsync weChatServiceAsync,
            @NotNull WeChatServiceRx weChatServiceRx)
    {
        super();
        this.weChatServiceAsync = weChatServiceAsync;
        this.weChatServiceRx = weChatServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Track Share">
    public Observable<TrackShareDTO> trackShareRx(
            @NotNull UserBaseKey userId,
            @NotNull WeChatTrackShareFormDTO weChatTrackShareFormDTO)
    {
        return weChatServiceRx.trackShare(userId.key, weChatTrackShareFormDTO);
    }

    public BaseMiddleCallback<TrackShareDTO> trackShare(
            @NotNull UserBaseKey userId,
            @NotNull WeChatTrackShareFormDTO weChatTrackShareFormDTO,
            @Nullable Callback<TrackShareDTO> callback)
    {
        BaseMiddleCallback<TrackShareDTO> middleCallback = new BaseMiddleCallback<>(callback);
        weChatServiceAsync.trackShare(userId.key, weChatTrackShareFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
