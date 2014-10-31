package com.tradehero.th.persistence.security;

import android.util.Pair;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class SecurityCompactCache extends StraightDTOCacheNew<SecurityId, SecurityCompactDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull protected final Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @NotNull protected final Lazy<SecurityCompactCacheRx> securityCompactCacheRx;
    @NotNull protected final SecurityIdCache securityIdCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityCompactCache(
            @NotNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NotNull Lazy<SecurityCompactCacheRx> securityCompactCacheRx,
            @NotNull SecurityIdCache securityIdCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.securityServiceWrapper = securityServiceWrapper;
        this.securityCompactCacheRx = securityCompactCacheRx;
        this.securityIdCache = securityIdCache;
    }
    //</editor-fold>

    @Override @NotNull public SecurityCompactDTO fetch(@NotNull SecurityId key) throws Throwable
    {
        SecurityCompactDTO securityCompactDTO = null;
        SecurityPositionDetailDTO securityPositionDetailDTO = securityServiceWrapper.get().getSecurity(key);

        if (securityPositionDetailDTO != null)
        {
            securityCompactDTO = securityPositionDetailDTO.security;
        }

        if (securityCompactDTO == null)
        {
            throw new NullPointerException("SecurityCompact was null for " + key);
        }
        return securityCompactDTO;
    }

    @Override public SecurityCompactDTO put(@NotNull SecurityId key, @NotNull SecurityCompactDTO value)
    {
        securityCompactCacheRx.get().onNext(key, value);
        // We save the correspondence between int id and exchange/symbol for future reference
        securityIdCache.onNext(value.getSecurityIntegerId(), key);
        return super.put(key, value);
    }

    @NotNull public SecurityCompactDTOList put(@NotNull List<SecurityCompactDTO> values)
    {
        SecurityCompactDTOList previousValues = new SecurityCompactDTOList();
        for (SecurityCompactDTO securityCompactDTO: values)
        {
            previousValues.add(put(securityCompactDTO.getSecurityId(), securityCompactDTO));
        }
        return previousValues;
    }

    @NotNull public SecurityCompactDTOList get(@NotNull List<SecurityId> keys)
    {
        SecurityCompactDTOList values = new SecurityCompactDTOList();

        for (SecurityId securityId: keys)
        {
            values.add(get(securityId));
        }

        return values;
    }

    @Nullable public SecurityCompactDTO get(@NotNull SecurityIntegerId id)
    {
        @Nullable SecurityId securityId = securityIdCache.get(id)
                .toBlocking()
                .firstOrDefault(Pair.create((SecurityIntegerId) null, (SecurityId) null))
                .second;
        if (securityId == null)
        {
            return null;
        }
        return get(securityId);
    }
}
