package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SecurityIdCache extends BaseDTOCacheRx<SecurityIntegerId, SecurityId>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 2000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 2;

    @Inject public SecurityIdCache()
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE);
    }
}
