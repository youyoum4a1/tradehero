package com.tradehero.th.api.users;

/**
 * Created by tradehero on 14-7-21.
 */
public class UpdateReferralCodeDTO
{
    public String inviteCode;

    public UpdateReferralCodeDTO(String inviteCode)
    {
        super();
        this.inviteCode = inviteCode;
    }
}
