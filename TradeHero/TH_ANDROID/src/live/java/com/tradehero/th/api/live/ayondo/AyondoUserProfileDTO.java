package com.tradehero.th.api.live.ayondo;

// TODO: temporary dummy DTO, pending server confirm

import com.tradehero.th.api.live.LiveUserProfileDTO;

public class AyondoUserProfileDTO extends LiveUserProfileDTO
{
    public AyondoUserProfileDTO(String accountId, String email)
    {
        super(1, accountId, email);
    }

    @Override public String toString()
    {
        return "AyondoUserProfileDTO {"
                + "accountId :" + accountId
                + " }";
    }
}
