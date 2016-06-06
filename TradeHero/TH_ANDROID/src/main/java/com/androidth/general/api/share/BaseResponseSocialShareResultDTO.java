package com.androidth.general.api.share;

import android.support.annotation.NonNull;
import com.androidth.general.api.BaseResponseDTO;

public class BaseResponseSocialShareResultDTO implements SocialShareResultDTO
{
    @NonNull public final BaseResponseDTO responseDTO;

    //<editor-fold desc="Constructors">
    public BaseResponseSocialShareResultDTO(@NonNull BaseResponseDTO responseDTO)
    {
        this.responseDTO = responseDTO;
    }
    //</editor-fold>
}
