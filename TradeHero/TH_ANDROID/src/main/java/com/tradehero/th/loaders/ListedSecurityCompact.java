package com.tradehero.th.loaders;

import com.tradehero.th.api.security.SecurityCompactDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 4:10 PM To change this template use File | Settings | File Templates. */
public class ListedSecurityCompact extends SecurityCompactDTO
{
    private int index;

    public ListedSecurityCompact(final SecurityCompactDTO securityCompactDTO)
    {
        super(securityCompactDTO);
    }

    public ListedSecurityCompact(final SecurityCompactDTO securityCompactDTO, final int index)
    {
        super(securityCompactDTO);
        this.index = index;
    }


}
