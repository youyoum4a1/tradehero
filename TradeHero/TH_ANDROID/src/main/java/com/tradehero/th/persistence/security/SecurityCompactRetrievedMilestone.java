package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import dagger.Lazy;
import javax.inject.Inject;


public class SecurityCompactRetrievedMilestone extends DTORetrievedAsyncMilestone<SecurityId, SecurityCompactDTO, SecurityCompactCache>
{
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;

    public SecurityCompactRetrievedMilestone(SecurityId key)
    {
        super(key);
    }

    @Override protected SecurityCompactCache getCache()
    {
        return securityCompactCache.get();
    }

    @Override public void launch()
    {
        launchOwn();
    }
}
