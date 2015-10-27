package com.tradehero.th.api.live.ayondo;

// TODO: temporary dummy DTO, pending server confirm

import com.tradehero.th.api.live.LivePortfolioDTO;
import com.tradehero.th.api.live.LiveUserProfileDTO;

public class AyondoUserProfileDTO extends LiveUserProfileDTO
{
    public AyondoUserProfileDTO()
    {
    }

    @Override public String toString()
    {
        return "[AyondoUserProfileDTO "
                + "accountId= " + accountId
                + ", " + livePortfolioDTO.toString()
                + "]";
    }

    // Dummy AyondoUserProfileDTO
    public static AyondoUserProfileDTO createForDummy()
    {
        AyondoUserProfileDTO ayondoUserProfileDTO = new AyondoUserProfileDTO();
        ayondoUserProfileDTO.accountId = "6627";
        ayondoUserProfileDTO.brokerId = 1;
        ayondoUserProfileDTO.email = "lihao@tradehero.mobi";
        ayondoUserProfileDTO.livePortfolioDTO = LivePortfolioDTO.createForDummy();

        return ayondoUserProfileDTO;
    }
}
