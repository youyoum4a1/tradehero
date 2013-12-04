package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.DTORetrievedMilestone;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 12/4/13 Time: 12:32 PM Copyright (c) TradeHero */
public class SecurityCompactRetrievedMilestone extends DTORetrievedMilestone<SecurityId, SecurityCompactDTO, SecurityCompactCache>
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
