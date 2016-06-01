package com.ayondo.academy.network.share.dto;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.share.SocialShareFormDTO;
import com.ayondo.academy.api.share.SocialShareResultDTO;

public class SharedSuccessful extends SocialShareResult
{
    @NonNull public final SocialShareResultDTO socialShareResultDTO;

    //<editor-fold desc="Constructors">
    public SharedSuccessful(
            @NonNull SocialShareFormDTO shareFormDTO,
            @NonNull SocialShareResultDTO socialShareResultDTO)
    {
        super(shareFormDTO);
        this.socialShareResultDTO = socialShareResultDTO;
    }
    //</editor-fold>
}
