package com.tradehero.common.persistence;

import android.support.annotation.NonNull;
import android.util.Pair;
import rx.functions.Action1;

public class ActionOnNextCache<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements Action1<Pair<DTOKeyType, DTOType>>
{
    @NonNull private final DTOCacheRx<DTOKeyType, DTOType> cacheRx;

    //<editor-fold desc="Constructors">
    public ActionOnNextCache(@NonNull DTOCacheRx<DTOKeyType, DTOType> cacheRx)
    {
        this.cacheRx = cacheRx;
    }
    //</editor-fold>

    @Override public void call(Pair<DTOKeyType, DTOType> pair)
    {
        cacheRx.onNext(pair.first, pair.second);
    }
}
