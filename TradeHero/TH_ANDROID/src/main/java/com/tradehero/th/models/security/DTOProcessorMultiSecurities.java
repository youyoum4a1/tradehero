package com.tradehero.th.models.security;

import android.support.annotation.Nullable;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
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
