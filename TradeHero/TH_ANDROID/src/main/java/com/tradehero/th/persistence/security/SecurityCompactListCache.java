package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.network.service.SecurityServiceUtil;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM */
@Singleton public class SecurityCompactListCache extends StraightDTOCache<SecurityListType, SecurityIdList>
{
    public static final String TAG = SecurityCompactListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected Lazy<SecurityService> securityService;
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
        return putInternal(key, SecurityServiceUtil.getSecurities(securityService.get(), key));
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
