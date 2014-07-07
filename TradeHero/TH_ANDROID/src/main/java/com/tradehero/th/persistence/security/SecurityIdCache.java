package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class SecurityIdCache extends StraightDTOCacheNew<SecurityIntegerId, SecurityId>
{
    public static final int DEFAULT_MAX_SIZE = 2000;

    @Inject public SecurityIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override @NotNull public SecurityId fetch(@NotNull SecurityIntegerId key) throws Throwable
    {
        throw new IllegalStateException("You should never fetch a SecurityId this way");
    }
}
