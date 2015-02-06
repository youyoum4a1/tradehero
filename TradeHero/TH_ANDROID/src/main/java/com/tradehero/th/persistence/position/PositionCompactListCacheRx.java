package com.tradehero.th.persistence.position;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class PositionCompactListCacheRx extends BaseFetchDTOCacheRx<SecurityId, PositionDTOCompactList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final SecurityServiceWrapper securityServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public PositionCompactListCacheRx(
            @NonNull DTOCacheUtilRx dtoCacheUtilRx,
            @NonNull SecurityServiceWrapper securityServiceWrapper)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtilRx);
        this.securityServiceWrapper = securityServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<PositionDTOCompactList> fetch(@NonNull SecurityId key)
    {
        return securityServiceWrapper.getSecurityPositionCompacts(key);
    }
}
