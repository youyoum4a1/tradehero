package com.androidth.general.persistence.live;

import android.support.annotation.NonNull;

import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.security.CompositeExchangeSecurityDTO;
import com.androidth.general.api.security.SecurityTypeDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.network.service.MarketServiceWrapper;
import com.androidth.general.network.service.ProviderServiceWrapper;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import rx.Observable;

@Singleton @UserCache
public class CompositeExchangeSecurityCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, CompositeExchangeSecurityDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public CompositeExchangeSecurityCacheRx(
            @NonNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull @RxLogObservable public Observable<CompositeExchangeSecurityDTO> fetch(@NonNull final UserBaseKey key)
    {
        return marketServiceWrapper.get().getLiveExchangeSecurityTypes();
    }

//    public void onNext(@NonNull CompositeExchangeSecurityDTO compositeExchangeSecurityDTO)
//    {
//        onNext(userBaseKey, compositeExchangeSecurityDTO);
//    }
}
