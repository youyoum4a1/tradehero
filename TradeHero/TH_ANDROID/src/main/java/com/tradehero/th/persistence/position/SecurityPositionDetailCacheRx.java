package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class SecurityPositionDetailCacheRx extends BaseFetchDTOCacheRx<SecurityId, SecurityPositionDetailDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull protected final Lazy<SecurityServiceWrapper> securityServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject protected SecurityPositionDetailCacheRx(
            @NotNull Lazy<SecurityServiceWrapper> securityServiceWrapper)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE);
        this.securityServiceWrapper = securityServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override protected Observable<SecurityPositionDetailDTO> fetch(@NotNull SecurityId key)
    {
        return securityServiceWrapper.get().getSecurityRx(key);
    }
}
