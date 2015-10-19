package com.tradehero.th.api.live;

// TODO: temporary dummy DTO, pending server confirm

public class LiveUserProfileDTO
{
    public int brokerId;
    public String accountId;
    public String email;

    public LiveUserProfileDTO(int brokerId, String accountId, String email)
    {
        this.brokerId = brokerId;
        this.accountId = accountId;
        this.email = email;
    }
}
