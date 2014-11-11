package com.tradehero.th.network.share;

import android.support.annotation.NonNull;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.List;

public interface SocialSharer
{
    void setSharedListener(OnSharedListener sharedListener);
    void share(@NonNull SocialShareFormDTO shareFormDTO);

    public static interface OnSharedListener
    {
        void onConnectRequired(SocialShareFormDTO shareFormDTO, List<SocialNetworkEnum> toConnect);
        void onShared(SocialShareFormDTO shareFormDTO, SocialShareResultDTO socialShareResultDTO);
        void onShareFailed(SocialShareFormDTO shareFormDTO, Throwable throwable);
    }
}
