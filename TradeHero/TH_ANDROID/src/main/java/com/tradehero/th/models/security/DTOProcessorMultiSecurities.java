package com.tradehero.th.models.security;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class DTOProcessorMultiSecurities implements DTOProcessor<Map<Integer, SecurityCompactDTO>>
{
    private final SecurityCompactCache securityCompactCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorMultiSecurities(SecurityCompactCache securityCompactCache)
    {
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @Override public Map<Integer, SecurityCompactDTO> process(@Nullable Map<Integer, SecurityCompactDTO> value)
    {
        if (value != null)
        {
            for (@Nullable SecurityCompactDTO securityCompactDTO: value.values())
            {
                if (securityCompactDTO != null)
                {
                    securityCompactCache.put(securityCompactDTO.getSecurityId(), securityCompactDTO);
                }
            }
        }
        return value;
    }
}
