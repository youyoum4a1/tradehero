package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class SecurityCompactListCacheRx extends BaseFetchDTOCacheRx<
        SecurityListType,
        SecurityCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NotNull private final Lazy<SecurityServiceWrapper> securityServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject protected SecurityCompactListCacheRx(
            @NotNull Lazy<SecurityServiceWrapper> securityServiceWrapper)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE);
        this.securityServiceWrapper = securityServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override protected Observable<SecurityCompactDTOList> fetch(@NotNull SecurityListType key)
    {
        return securityServiceWrapper.get().getSecuritiesRx(key);
    }
}
