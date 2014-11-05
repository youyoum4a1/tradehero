package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import rx.Observable;

@Singleton @UserCache
public class SecurityCompactListCacheRx extends BaseFetchDTOCacheRx<
        SecurityListType,
        SecurityCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;
    public static final int DEFAULT_MAX_FETCHER_SIZE = 5;

    @NonNull private final Lazy<SecurityServiceWrapper> securityServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject protected SecurityCompactListCacheRx(
            @NonNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_FETCHER_SIZE, dtoCacheUtil);
        this.securityServiceWrapper = securityServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<SecurityCompactDTOList> fetch(@NonNull SecurityListType key)
    {
        return securityServiceWrapper.get().getSecuritiesRx(key);
    }
}
