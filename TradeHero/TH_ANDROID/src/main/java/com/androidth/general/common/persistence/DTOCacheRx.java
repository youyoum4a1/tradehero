package com.androidth.general.common.persistence;

import android.support.annotation.NonNull;
import android.util.Pair;
import rx.Observable;

public interface DTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
{
    @NonNull Observable<Pair<DTOKeyType, DTOType>> get(@NonNull DTOKeyType key);
    void onNext(DTOKeyType key, DTOType value);

    void invalidate(@NonNull DTOKeyType key);
    void invalidateAll();
}
