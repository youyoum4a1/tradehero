package com.tradehero.th.api.social;

public class InviteFacebookDTO implements InviteDTO
{
    public String fbId;

    //<editor-fold desc="Constructors">
    public InviteFacebookDTO()
    {
        super();
    }

    public InviteFacebookDTO(String fbId)
    {
        this.fbId = fbId;
    }
    //</editor-fold>
}
