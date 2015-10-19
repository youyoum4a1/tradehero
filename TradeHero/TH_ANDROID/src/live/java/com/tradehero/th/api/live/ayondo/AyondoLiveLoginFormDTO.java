package com.tradehero.th.api.live.ayondo;

import com.tradehero.th.api.live.LiveLoginFormDTO;

// TODO: temporary dummy DTO, pending server confirm

public class AyondoLiveLoginFormDTO extends LiveLoginFormDTO
{
    public AyondoLiveLoginFormDTO(String accountId, String password)
    {
        super(1, accountId, password);
    }
}
