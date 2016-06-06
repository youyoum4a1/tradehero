package com.androidth.general.common.persistence;

import android.support.annotation.NonNull;
import android.util.Pair;
import rx.Observable;
import rx.functions.Func1;

public class GetOneFromCache<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements Func1<DTOKeyType, Observable<Pair<DTOKeyType, DTOType>>>
{
    @NonNull private final BaseDTOCacheRx<DTOKeyType, DTOType> cache;

    //<editor-fold desc="Constructors">
    public GetOneFromCache(@NonNull BaseDTOCacheRx<DTOKeyType, DTOType> cache)
    {
        this.cache = cache;
    }
    //</editor-fold>

    @Override public Observable<Pair<DTOKeyType, DTOType>> call(DTOKeyType keyType)
    {
        return cache.getOne(keyType);
    }
}
