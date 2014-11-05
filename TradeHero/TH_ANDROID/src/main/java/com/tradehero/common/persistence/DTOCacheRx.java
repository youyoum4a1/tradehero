package com.tradehero.common.persistence;

import android.util.Pair;
import android.support.annotation.NonNull;
import rx.Observable;

public interface DTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
{
    @NonNull Observable<Pair<DTOKeyType, DTOType>> get(@NonNull DTOKeyType key);
    void onNext(DTOKeyType key, DTOType value);

    void invalidate(@NonNull DTOKeyType key);
    void invalidateAll();
}
