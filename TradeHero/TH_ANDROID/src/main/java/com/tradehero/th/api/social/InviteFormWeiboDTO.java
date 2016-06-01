package com.ayondo.academy.api.social;

public class InviteFormWeiboDTO extends InviteFormMessageDTO
{
    public final boolean isWeiboInvite = true;

    //<editor-fold desc="Constructors">
    public InviteFormWeiboDTO(String msg)
    {
        super(msg);
    }
    //</editor-fold>
}
