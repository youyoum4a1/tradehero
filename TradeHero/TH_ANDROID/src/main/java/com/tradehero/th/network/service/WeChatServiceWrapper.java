package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.share.TrackShareDTO;
import com.ayondo.academy.api.share.wechat.WeChatTrackShareFormDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class WeChatServiceWrapper
{
    @NonNull private final WeChatServiceRx weChatServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public WeChatServiceWrapper(
            @NonNull WeChatServiceRx weChatServiceRx)
    {
        super();
        this.weChatServiceRx = weChatServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Track Share">
    @NonNull public Observable<TrackShareDTO> trackShareRx(
            @NonNull UserBaseKey userId,
            @NonNull WeChatTrackShareFormDTO weChatTrackShareFormDTO)
    {
        return weChatServiceRx.trackShare(userId.key, weChatTrackShareFormDTO);
    }
    //</editor-fold>
}
