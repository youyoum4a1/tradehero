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

@Singleton public class SecurityCompactListCache extends StraightDTOCache<SecurityListType, SecurityIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityCompactListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected SecurityIdList fetch(SecurityListType key) throws Throwable
    {
        //THLog.d(TAG, "fetch " + key);
        return putInternal(key, securityServiceWrapper.get().getSecurities(key));
    }

    protected SecurityIdList putInternal(SecurityListType key, List<SecurityCompactDTO> fleshedValues)
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
