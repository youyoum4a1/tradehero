package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class SecurityCompactListCache extends StraightCutDTOCacheNew<
        SecurityListType,
        SecurityCompactDTOList,
        SecurityIdList>
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

    @Override @NotNull public SecurityCompactDTOList fetch(@NotNull SecurityListType key) throws Throwable
    {
        return securityServiceWrapper.get().getSecurities(key);
    }

    @NotNull @Override protected SecurityIdList cutValue(@NotNull SecurityListType key, @NotNull SecurityCompactDTOList value)
    {
        securityCompactCache.get().put(value);
        return new SecurityIdList(value, (SecurityCompactDTO) null);
    }

    @Nullable @Override protected SecurityCompactDTOList inflateValue(@NotNull SecurityListType key, @Nullable SecurityIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        SecurityCompactDTOList value = securityCompactCache.get().get(cutValue);
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
