package com.androidth.general.persistence.security;

import android.support.annotation.NonNull;
import android.util.Log;

import com.androidth.general.api.security.key.TrendingSecurityListType;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.security.SecurityCompactDTOList;
import com.androidth.general.api.security.key.SecurityListType;
import com.androidth.general.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class SecurityCompactListCacheRx extends BaseFetchDTOCacheRx<
        SecurityListType,
        SecurityCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject protected SecurityCompactListCacheRx(
            @NonNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.securityServiceWrapper = securityServiceWrapper;
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<SecurityCompactDTOList> fetch(@NonNull SecurityListType key)
    {
        if(key instanceof TrendingSecurityListType){
            if(((TrendingSecurityListType) key).exchange==null){
                Log.v("Security", "!!!!! empty");
                return Observable.empty();
            }
        }
        return securityServiceWrapper.get().getSecuritiesRxMainThread(key);
    }

    @Override public void onNext(@NonNull SecurityListType key, @NonNull SecurityCompactDTOList value)
    {
        securityCompactCache.get().onNext(value);
        super.onNext(key, value);
    }
}
