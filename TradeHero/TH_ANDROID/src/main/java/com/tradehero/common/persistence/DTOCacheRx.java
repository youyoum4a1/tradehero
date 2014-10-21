package com.tradehero.common.persistence;

import android.util.Pair;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public interface DTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
{
    @NotNull Observable<Pair<DTOKeyType, DTOType>> get(@NotNull DTOKeyType key);

    void invalidate(@NotNull DTOKeyType key);
    void invalidateAll();
}
