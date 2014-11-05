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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Singleton @UserCache @Deprecated
public class SecurityCompactListCache extends StraightCutDTOCacheNew<
        SecurityListType,
        SecurityCompactDTOList,
        SecurityIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NonNull private final Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityCompactListCache(
            @NonNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.securityServiceWrapper = securityServiceWrapper;
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @Override @NonNull public SecurityCompactDTOList fetch(@NonNull SecurityListType key) throws Throwable
    {
        return securityServiceWrapper.get().getSecurities(key);
    }

    @NonNull @Override protected SecurityIdList cutValue(@NonNull SecurityListType key, @NonNull SecurityCompactDTOList value)
    {
        securityCompactCache.get().onNext(value);
        return new SecurityIdList(value, (SecurityCompactDTO) null);
    }

    @Nullable @Override protected SecurityCompactDTOList inflateValue(@NonNull SecurityListType key, @Nullable SecurityIdList cutValue)
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
