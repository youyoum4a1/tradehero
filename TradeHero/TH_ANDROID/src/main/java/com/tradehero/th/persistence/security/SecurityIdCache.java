package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton public class SecurityIdCache extends StraightDTOCache<SecurityIntegerId, SecurityId>
{
    public static final int DEFAULT_MAX_SIZE = 2000;

    @Inject public SecurityIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected SecurityId fetch(SecurityIntegerId key) throws Throwable
    {
        throw new IllegalStateException("You should never fetch a SecurityId this way");
    }
}
