package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class SecurityCompactListCache extends StraightDTOCache<SecurityListType, SecurityIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @NotNull private final Lazy<SecurityCompactCache> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityCompactListCache(
            @NotNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NotNull Lazy<SecurityCompactCache> securityCompactCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.securityServiceWrapper = securityServiceWrapper;
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @Override protected SecurityIdList fetch(@NotNull SecurityListType key) throws Throwable
    {
        return putInternal(key, securityServiceWrapper.get().getSecurities(key));
    }

    @Nullable protected SecurityIdList putInternal(
            @NotNull SecurityListType key,
            @Nullable List<SecurityCompactDTO> fleshedValues)
    {
        SecurityIdList securityIds = null;
        if (fleshedValues != null)
        {
            securityIds = new SecurityIdList();
            SecurityId securityId;
            for (SecurityCompactDTO securityCompactDTO: fleshedValues)
            {
                securityId = securityCompactDTO.getSecurityId();
                securityIds.add(securityId);
                securityCompactCache.get().put(securityId, securityCompactDTO);
            }
            put(key, securityIds);
        }
        return securityIds;
    }
}
