package com.tradehero.common.persistence;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class BaseDTOListCache<
        DTOKeyType extends DTOKey,
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>>
        extends BaseDTOCache<DTOKeyType, DTOListType>
{
    //<editor-fold desc="Constructors">
    protected BaseDTOListCache(int maxSize)
    {
        super(maxSize);
    }
    //</editor-fold>

    @NotNull public Observable<DTOType> createListObservable(@NotNull DTOKeyType key)
    {
        return Observable.from(get(key));
    }
}
