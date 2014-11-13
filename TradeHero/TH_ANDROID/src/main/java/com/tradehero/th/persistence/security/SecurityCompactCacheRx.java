package com.tradehero.th.persistence.security;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class SecurityCompactCacheRx extends BaseDTOCacheRx<SecurityId, SecurityCompactDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NonNull private final SecurityIdCache securityIdCache;

    //<editor-fold desc="Constructors">
    @Inject protected SecurityCompactCacheRx(
            @NonNull SecurityIdCache securityIdCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.securityIdCache = securityIdCache;
    }
    //</editor-fold>

    @Override protected SecurityCompactDTO putValue(
            @NonNull SecurityId key, @NonNull SecurityCompactDTO value)
    {
        SecurityCompactDTO previous = super.putValue(key, value);
        securityIdCache.onNext(value.getSecurityIntegerId(), key);
        return previous;
    }

    public void onNext(@NonNull List<? extends SecurityCompactDTO> securityCompacts)
    {
        for (SecurityCompactDTO securityCompact : securityCompacts)
        {
            onNext(securityCompact.getSecurityId(), securityCompact);
        }
    }
}
