package com.androidth.general.models.position;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionDTOCompact;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.SecurityIntegerId;
import rx.Observable;
import rx.functions.Func1;

public class PositionDTOUtils
{
    @NonNull public static Observable<Pair<PositionDTO, SecurityCompactDTO>> getSecuritiesSoft(
            @NonNull Observable<PositionDTO> positionDTOs,
            @NonNull final BaseDTOCacheRx<SecurityIntegerId, SecurityId> securityIdCache,
            @NonNull final BaseDTOCacheRx<SecurityId, SecurityCompactDTO> securityCompactCache)
    {
        return getSecuritiesSoft(
                getSecurityIdsSoft(positionDTOs, securityIdCache),
                securityCompactCache);
    }

    @NonNull public static Observable<Pair<PositionDTO, SecurityId>> getSecurityIdsSoft(
            @NonNull Observable<PositionDTO> positionDTOs,
            @NonNull final BaseDTOCacheRx<SecurityIntegerId, SecurityId> securityIdCache)
    {
        return positionDTOs.flatMap(new Func1<PositionDTO, Observable<Pair<PositionDTO, SecurityId>>>()
        {
            @Override public Observable<Pair<PositionDTO, SecurityId>> call(final PositionDTO positionDTO)
            {
                if (positionDTO.securityId <= 0)
                {
                    return Observable.just(Pair.create(positionDTO, (SecurityId) null));
                }
                return securityIdCache.getOne(positionDTO.getSecurityIntegerId())
                        .map(new Func1<Pair<SecurityIntegerId, SecurityId>, Pair<PositionDTO, SecurityId>>()
                        {
                            @Override public Pair<PositionDTO, SecurityId> call(Pair<SecurityIntegerId, SecurityId> idPair)
                            {
                                return Pair.create(positionDTO, idPair.second);
                            }
                        });
            }
        });
    }

    @NonNull public static <PositionType extends PositionDTOCompact> Observable<Pair<PositionType, SecurityCompactDTO>> getSecuritiesSoft(
            @NonNull Observable<Pair<PositionType, SecurityId>> securityIds,
            @NonNull final BaseDTOCacheRx<SecurityId, SecurityCompactDTO> securityCompactCache)
    {
        return securityIds.flatMap(new Func1<Pair<PositionType, SecurityId>, Observable<Pair<PositionType, SecurityCompactDTO>>>()
        {
            @Override public Observable<Pair<PositionType, SecurityCompactDTO>> call(final Pair<PositionType, SecurityId> positionPair)
            {
                if (positionPair.second == null)
                {
                    return Observable.just(Pair.create(positionPair.first, (SecurityCompactDTO) null));
                }
                return securityCompactCache.getOne(positionPair.second)
                        .map(new Func1<Pair<SecurityId, SecurityCompactDTO>, Pair<PositionType, SecurityCompactDTO>>()
                        {
                            @Override
                            public Pair<PositionType, SecurityCompactDTO> call(
                                    Pair<SecurityId, SecurityCompactDTO> securityCompactPair)
                            {
                                return Pair.create(positionPair.first, securityCompactPair.second);
                            }
                        });
            }
        });
    }
}
