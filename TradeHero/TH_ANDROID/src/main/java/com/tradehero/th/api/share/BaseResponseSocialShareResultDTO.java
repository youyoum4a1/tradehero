package com.tradehero.th.api.share;

import com.tradehero.th.api.BaseResponseDTO;

public class BaseResponseSocialShareResultDTO implements SocialShareResultDTO
{
    public final BaseResponseDTO responseDTO;

    //<editor-fold desc="Constructors">
    public BaseResponseSocialShareResultDTO(BaseResponseDTO responseDTO)
    {
        this.responseDTO = responseDTO;
    }
    //</editor-fold>
}
