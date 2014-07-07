package com.tradehero.th.api.social;

public class InviteTwitterDTO implements InviteDTO
{
    public String twId;

    //<editor-fold desc="Constructors">
    public InviteTwitterDTO()
    {
        super();
    }

    public InviteTwitterDTO(String twId)
    {
        this.twId = twId;
    }
    //</editor-fold>
}
