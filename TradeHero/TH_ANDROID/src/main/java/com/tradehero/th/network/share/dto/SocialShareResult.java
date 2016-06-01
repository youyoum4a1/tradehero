package com.ayondo.academy.network.share.dto;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.share.SocialShareFormDTO;

public class SocialShareResult implements SocialDialogResult
{
    @NonNull public final SocialShareFormDTO shareFormDTO;

    //<editor-fold desc="Constructors">
    public SocialShareResult(@NonNull SocialShareFormDTO shareFormDTO)
    {
        this.shareFormDTO = shareFormDTO;
    }
    //</editor-fold>
}
