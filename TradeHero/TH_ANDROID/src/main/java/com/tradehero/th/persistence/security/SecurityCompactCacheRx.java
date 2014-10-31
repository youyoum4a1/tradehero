package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class SecurityCompactCacheRx extends BaseDTOCacheRx<SecurityId, SecurityCompactDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull private final SecurityIdCache securityIdCache;

    //<editor-fold desc="Constructors">
    @Inject protected SecurityCompactCacheRx(
            @NotNull SecurityIdCache securityIdCache)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE);
        this.securityIdCache = securityIdCache;
    }
    //</editor-fold>

    @Override protected SecurityCompactDTO putValue(
            @NotNull SecurityId key, @NotNull SecurityCompactDTO value)
    {
        SecurityCompactDTO previous = super.putValue(key, value);
        securityIdCache.onNext(value.getSecurityIntegerId(), key);
        return previous;
    }
}
