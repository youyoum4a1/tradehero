package com.tradehero.th.models.security;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOFactory;
import com.tradehero.th.models.DTOProcessor;

import java.util.List;

public class DTOProcessorSecurityCompactListReceived implements DTOProcessor<List<SecurityCompactDTO>>
{
    private final SecurityCompactDTOFactory securityCompactDTOFactory;

    public DTOProcessorSecurityCompactListReceived(SecurityCompactDTOFactory securityCompactDTOFactory)
    {
        this.securityCompactDTOFactory = securityCompactDTOFactory;
    }

    @Override
    public List<SecurityCompactDTO> process(List<SecurityCompactDTO> value)
    {
        return securityCompactDTOFactory.clonePerType(value);
    }
}
