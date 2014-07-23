package com.tradehero.th.api.social;

import java.util.List;

public class InviteFormDTO
{
    public String msg;
    public List<InviteDTO> users;
    public boolean isWeiboInvite = false;

    public InviteFormDTO()
    {
        msg = "";
    }

    public InviteFormDTO(String msg,boolean isWeiboInvite)
    {
        this.msg = msg;
        this.isWeiboInvite = isWeiboInvite;
    }
}
