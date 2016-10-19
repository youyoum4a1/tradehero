package com.androidth.general.persistence.security;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.api.security.SecurityCompositeDTO;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class SecurityCompactCacheRx extends BaseFetchDTOCacheRx<SecurityId, SecurityCompactDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    @NonNull private final Lazy<SecurityIdCache> securityIdCache;
    private Lazy<SecurityServiceWrapper> securityServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject protected SecurityCompactCacheRx(
            @NonNull Lazy<SecurityIdCache> securityIdCache,
            @NonNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.securityIdCache = securityIdCache;
        this.securityServiceWrapper = securityServiceWrapper;
    }
    //</editor-fold>

    @Override @Nullable protected SecurityCompactDTO putValue(
            @NonNull SecurityId key, @NonNull SecurityCompactDTO value)
    {
        SecurityCompactDTO previous = super.putValue(key, value);
        securityIdCache.get().onNext(value.getSecurityIntegerId(), key);
        return previous;
    }

    public void onNext(@NonNull List<? extends SecurityCompactDTO> securityCompacts)
    {
        for (SecurityCompactDTO securityCompact : securityCompacts)
        {
            onNext(securityCompact.getSecurityId(), securityCompact);
//            fetch(securityCompact.getSecurityId());
        }
    }

    public void onNext(@NonNull SecurityCompositeDTO securityCompositeDTO)
    {
        for (SecurityCompactDTO securityCompact : securityCompositeDTO.Securities)
        {
            onNext(securityCompact.getSecurityId(), securityCompact);//must not do fetching
        }
    }

    @NonNull @Override protected Observable<SecurityCompactDTO> fetch(@NonNull SecurityId key)
    {
        return securityServiceWrapper.get().getSecurityCompactRx(key);
    }
}
