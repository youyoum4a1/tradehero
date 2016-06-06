package com.androidth.general.network.share.dto;

import android.support.annotation.NonNull;
import com.androidth.general.api.share.SocialShareFormDTO;

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
