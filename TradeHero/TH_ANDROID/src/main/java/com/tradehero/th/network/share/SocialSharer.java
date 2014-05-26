package com.tradehero.th.network.share;

import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;

public interface SocialSharer
{
    void setSharedListener(OnSharedListener sharedListener);
    void share(SocialShareFormDTO shareFormDTO, OnSharedListener sharedListener);

    public static interface OnSharedListener
    {
        void onShared(SocialShareFormDTO shareFormDTO, SocialShareResultDTO socialShareResultDTO);
        void onShareFailed(SocialShareFormDTO shareFormDTO, Throwable throwable);
    }
}
