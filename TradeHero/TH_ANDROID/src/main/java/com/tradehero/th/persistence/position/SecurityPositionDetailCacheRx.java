package com.tradehero.th.persistence.position;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class SecurityPositionDetailCacheRx extends BaseFetchDTOCacheRx<SecurityId, SecurityPositionDetailDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;
    public static final int DEFAULT_MAX_FETCHER_SIZE = 10;

    @NonNull protected final SecurityServiceWrapper securityServiceWrapper;
    @NonNull protected final Lazy<SecurityCompactCacheRx> securityCompactCacheRx;

    //<editor-fold desc="Constructors">
    @Inject protected SecurityPositionDetailCacheRx(
            @NonNull SecurityServiceWrapper securityServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCacheRx,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_FETCHER_SIZE, dtoCacheUtil);
        this.securityServiceWrapper = securityServiceWrapper;
        this.securityCompactCacheRx = securityCompactCacheRx;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<SecurityPositionDetailDTO> fetch(@NonNull SecurityId key)
    {
        return securityServiceWrapper.getSecurityPositionDetailRx(key);
    }

    @Override public void onNext(@NonNull SecurityId key, @NonNull SecurityPositionDetailDTO value)
    {
        super.onNext(key, value);
        securityCompactCacheRx.get().onNext(key, value.security);
    }
}
