package com.tradehero.th.api.live;

// TODO: temporary dummy DTO, pending server confirm

public class LiveLoginFormDTO
{
    public final int brokerId;
    public final String accountId;
    public final String password;

    public LiveLoginFormDTO(int brokerId, String accountId, String password)
    {
        this.brokerId = brokerId;
        this.accountId = accountId;
        this.password = password;
    }
}
