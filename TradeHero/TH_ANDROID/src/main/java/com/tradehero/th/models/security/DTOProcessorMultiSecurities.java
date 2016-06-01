package com.ayondo.academy.models.security;

import android.support.annotation.Nullable;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.security.SecurityCompactCacheRx;
import java.util.Map;

public class DTOProcessorMultiSecurities extends ThroughDTOProcessor<Map<Integer, SecurityCompactDTO>>
{
    private final SecurityCompactCacheRx securityCompactCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorMultiSecurities(SecurityCompactCacheRx securityCompactCache)
    {
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @Override public Map<Integer, SecurityCompactDTO> process(@Nullable Map<Integer, SecurityCompactDTO> value)
    {
        if (value != null)
        {
            for (SecurityCompactDTO securityCompactDTO: value.values())
            {
                if (securityCompactDTO != null)
                {
                    securityCompactCache.onNext(securityCompactDTO.getSecurityId(), securityCompactDTO);
                }
            }
        }
        return value;
    }
}
