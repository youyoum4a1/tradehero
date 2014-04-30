package com.tradehero.th.loaders;

import com.tradehero.th.api.security.SecurityCompactDTO;

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
