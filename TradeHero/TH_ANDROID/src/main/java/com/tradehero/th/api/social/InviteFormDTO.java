package com.tradehero.th.api.social;

import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 8:57 PM To change this template use File | Settings | File Templates. */
public class InviteFormDTO
{
    public String msg;
    public List<InviteDTO> users;

    public InviteFormDTO()
    {
        msg = "";
    }
}
