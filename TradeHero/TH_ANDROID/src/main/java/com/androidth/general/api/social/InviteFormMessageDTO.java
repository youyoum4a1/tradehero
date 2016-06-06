package com.androidth.general.api.social;


abstract class InviteFormMessageDTO implements InviteFormDTO
{
    public String msg;

    //<editor-fold desc="Constructors">
    protected InviteFormMessageDTO(String msg)
    {
        this.msg = msg;
    }
    //</editor-fold>
}
