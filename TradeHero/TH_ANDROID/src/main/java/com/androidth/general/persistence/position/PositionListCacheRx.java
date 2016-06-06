package com.androidth.general.persistence.position;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionDTOList;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Action1;

@Singleton @UserCache
public class PositionListCacheRx extends BaseFetchDTOCacheRx<SecurityId, PositionDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final SecurityServiceWrapper securityServiceWrapper;
    @NonNull private final Lazy<PositionCacheRx> positionCache;

    //<editor-fold desc="Constructors">
    @Inject public PositionListCacheRx(
            @NonNull DTOCacheUtilRx dtoCacheUtilRx,
            @NonNull SecurityServiceWrapper securityServiceWrapper,
            @NonNull Lazy<PositionCacheRx> positionCache)

    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtilRx);
        this.securityServiceWrapper = securityServiceWrapper;
        this.positionCache = positionCache;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<PositionDTOList> fetch(@NonNull SecurityId key)
    {
        return securityServiceWrapper.getSecurityPositions(key)
                .doOnNext(new Action1<PositionDTOList>()
                {
                    @Override public void call(PositionDTOList positionDTOs)
                    {
                        for (PositionDTO positionDTO: positionDTOs)
                        {
                            positionCache.get().onNext(positionDTO.getOwnedPositionId(), positionDTO);
                        }
                    }
                });
    }
}
