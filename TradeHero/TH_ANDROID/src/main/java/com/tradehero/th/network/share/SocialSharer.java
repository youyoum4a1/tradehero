package com.tradehero.th.network.share;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.List;

public interface SocialSharer
{
    void setSharedListener(@Nullable OnSharedListener sharedListener);
    void share(@NonNull SocialShareFormDTO shareFormDTO);

    public static interface OnSharedListener
    {
        void onConnectRequired(@NonNull SocialShareFormDTO shareFormDTO, @NonNull List<SocialNetworkEnum> toConnect);
        void onShared(@NonNull SocialShareFormDTO shareFormDTO, @NonNull SocialShareResultDTO socialShareResultDTO);
        void onShareFailed(@NonNull SocialShareFormDTO shareFormDTO, @NonNull Throwable throwable);
    }
}
