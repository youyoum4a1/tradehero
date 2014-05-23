package com.tradehero.th.network.share;

import com.tradehero.th.api.share.SocialShareFormDTO;

public interface SocialSharer
{
    void setSharedListener(OnSharedListener sharedListener);
    void share(SocialShareFormDTO shareFormDTO, OnSharedListener sharedListener);

    public static interface OnSharedListener
    {
        // TODO add a DTO
        void onShared();
        void onShareFailed(Throwable throwable);
    }
}
