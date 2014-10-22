package com.tradehero.common.persistence;

import android.util.Pair;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.functions.Func1;
import rx.observers.EmptyObserver;
import rx.subjects.BehaviorSubject;

abstract public class BaseFetchDTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends BaseDTOCacheRx<DTOKeyType, DTOType>
{
    //<editor-fold desc="Constructors">
    protected BaseFetchDTOCacheRx(int valueSize, int subjectSize)
    {
        super(valueSize, subjectSize);
    }
    //</editor-fold>

    @NotNull abstract protected Observable<DTOType> fetch(@NotNull DTOKeyType key);

    @NotNull @Override
    public Observable<Pair<DTOKeyType, DTOType>> get(@NotNull final DTOKeyType key)
    {
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = getOrCreateBehavior(key);
        fetch(key)
                .map(new Func1<DTOType, Pair<DTOKeyType, DTOType>>()
                {
                    @Override public Pair<DTOKeyType, DTOType> call(DTOType dtoType)
                    {
                        return Pair.create(key, dtoType);
                    }
                })
                .subscribe(new EmptyObserver<Pair<DTOKeyType,DTOType>>()
                {
                    @Override public void onNext(Pair<DTOKeyType, DTOType> pair)
                    {
                        BaseFetchDTOCacheRx.this.onNext(pair.first, pair.second);
                    }
                });
        return cachedSubject;
    }
}
