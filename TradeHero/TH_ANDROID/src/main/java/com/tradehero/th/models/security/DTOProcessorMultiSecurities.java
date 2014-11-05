package com.tradehero.th.models.security;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import java.util.Map;
import android.support.annotation.Nullable;
import rx.functions.Action1;

public class DTOProcessorMultiSecurities implements DTOProcessor<Map<Integer, SecurityCompactDTO>>,
        Action1<Map<Integer, SecurityCompactDTO>>
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

    @Override public void call(Map<Integer, SecurityCompactDTO> map)
    {
        process(map);
    }
}
