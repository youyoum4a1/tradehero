package com.tradehero.th.persistence.security;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOFactory;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class SecurityCompactCache extends StraightDTOCache<SecurityId, SecurityCompactDTO>
{
    public static final String TAG = SecurityCompactCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @Inject protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject protected SecurityIdCache securityIdCache;
    @Inject protected SecurityCompactDTOFactory securityCompactDTOFactory;

    //<editor-fold desc="Constructors">
    @Inject public SecurityCompactCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected SecurityCompactDTO fetch(SecurityId key) throws Throwable
    {
        SecurityCompactDTO securityCompactDTO = null;
        SecurityPositionDetailDTO securityPositionDetailDTO = securityServiceWrapper.get().getSecurity(key);

        if (securityPositionDetailDTO != null)
        {
            securityPositionDetailCache.get().put(key, securityPositionDetailDTO);
            securityCompactDTO = securityPositionDetailDTO.security;
        }

        return securityCompactDTO;
    }

    public List<SecurityCompactDTO> getOrFetch(List<SecurityId> securityIds) throws Throwable
    {
        return getOrFetch(securityIds, false);
    }

    public List<SecurityCompactDTO> getOrFetch(List<SecurityId> securityIds, boolean force) throws Throwable
    {
        if (securityIds == null)
        {
            return null;
        }

        List<SecurityCompactDTO> securityCompactDTOList = new ArrayList<>();
        for (SecurityId securityId: securityIds)
        {
            securityCompactDTOList.add(getOrFetch(securityId, force));
        }
        return securityCompactDTOList;
    }

    @Override public SecurityCompactDTO put(SecurityId key, SecurityCompactDTO value)
    {
        // We save the correspondence between int id and exchange/symbol for future reference
        securityIdCache.put(value.getSecurityIntegerId(), key);

        // We make sure the proper type is recreated on the fly.
        return super.put(key, securityCompactDTOFactory.clonePerType(value));
    }

    public List<SecurityCompactDTO> put(List<SecurityCompactDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<SecurityCompactDTO> previousValues = new ArrayList<>();

        for (SecurityCompactDTO securityCompactDTO: values)
        {
            previousValues.add(put(securityCompactDTO.getSecurityId(), securityCompactDTO));
        }

        return previousValues;
    }

    public ArrayList<SecurityCompactDTO> get(List<SecurityId> keys)
    {
        if (keys == null)
        {
            return null;
        }

        ArrayList<SecurityCompactDTO> values = new ArrayList<>();

        for (SecurityId securityId: keys)
        {
            values.add(get(securityId));
        }

        return values;
    }
}
