package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class SecurityCompactCache extends StraightDTOCache<SecurityId, SecurityCompactDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull protected final Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @NotNull protected final Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @NotNull protected final SecurityIdCache securityIdCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityCompactCache(
            @NotNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NotNull Lazy<SecurityPositionDetailCache> securityPositionDetailCache,
            @NotNull SecurityIdCache securityIdCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.securityServiceWrapper = securityServiceWrapper;
        this.securityPositionDetailCache = securityPositionDetailCache;
        this.securityIdCache = securityIdCache;
    }
    //</editor-fold>

    @Override protected SecurityCompactDTO fetch(SecurityId key) throws Throwable
    {
        SecurityCompactDTO securityCompactDTO = null;
        SecurityPositionDetailDTO securityPositionDetailDTO = securityServiceWrapper.get().getSecurity(key);

        if (securityPositionDetailDTO != null)
        {
            securityPositionDetailCache.get().put(key, securityPositionDetailDTO);

            // We do a get again here because the put may have cloned into subclasses.
            // And we want the subclass
            securityCompactDTO = get(key);
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
        return super.put(key, value);
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
