package com.tradehero.common.persistence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;

public interface DTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
{
    public static final boolean DEFAULT_FORCE_UPDATE = false;

    @Nullable DTOType put(@NotNull DTOKeyType key, @NotNull DTOType value);
    @Nullable DTOType get(@NotNull DTOKeyType key);
    @NotNull Observable<DTOType> createObservable(@NotNull DTOKeyType key);

    void invalidate(@NotNull DTOKeyType key);
    void invalidateAll();
}
