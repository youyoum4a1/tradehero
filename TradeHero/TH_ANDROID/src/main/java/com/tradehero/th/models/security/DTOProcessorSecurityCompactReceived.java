package com.tradehero.th.models.security;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOFactory;
import com.tradehero.th.models.DTOProcessor;

public class DTOProcessorSecurityCompactReceived implements DTOProcessor<SecurityCompactDTO>
{
    private final SecurityCompactDTOFactory securityCompactDTOFactory;

    public DTOProcessorSecurityCompactReceived(SecurityCompactDTOFactory securityCompactDTOFactory)
    {
        this.securityCompactDTOFactory = securityCompactDTOFactory;
    }

    @Override
    public SecurityCompactDTO process(SecurityCompactDTO value)
    {
        return securityCompactDTOFactory.clonePerType(value);
    }
}
