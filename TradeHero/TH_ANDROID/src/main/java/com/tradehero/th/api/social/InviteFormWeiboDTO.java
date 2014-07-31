package com.tradehero.th.api.social;

public class InviteFormWeiboDTO extends InviteFormMessageDTO implements InviteFormDTO
{
    public final boolean isWeiboInvite = true;
    //<editor-fold desc="Constructors">
    public InviteFormWeiboDTO(String msg)
    {
        this.msg = msg;
    }
    //</editor-fold>
}
