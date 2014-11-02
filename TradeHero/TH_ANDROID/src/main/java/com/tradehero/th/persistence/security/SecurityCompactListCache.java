package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @UserCache @Deprecated
public class SecurityCompactListCache extends StraightCutDTOCacheNew<
        SecurityListType,
        SecurityCompactDTOList,
        SecurityIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @NotNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityCompactListCache(
            @NotNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NotNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
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
        securityCompactCache.get().onNext(value);
        return new SecurityIdList(value, (SecurityCompactDTO) null);
    }

    @Nullable @Override protected SecurityCompactDTOList inflateValue(@NotNull SecurityListType key, @Nullable SecurityIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        SecurityCompactDTOList value = new SecurityCompactDTOList();
        for (SecurityId securityId: cutValue)
        {
            value.add(securityCompactCache.get().getValue(securityId));
        }
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
