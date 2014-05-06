package com.tradehero.th.api.social;

import java.util.List;


public class InviteFormDTO
{
    public String msg;
    public List<InviteDTO> users;

    public InviteFormDTO()
    {
        msg = "";
    }
}
