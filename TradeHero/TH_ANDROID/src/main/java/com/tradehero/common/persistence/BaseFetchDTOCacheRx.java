package com.tradehero.common.persistence;

import android.util.Pair;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

abstract public class BaseFetchDTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends BaseDTOCacheRx<DTOKeyType, DTOType>
{
    @NotNull final private THLruCache<DTOKeyType, Subscription> cachedFetcherSubscriptions;

    //<editor-fold desc="Constructors">
    protected BaseFetchDTOCacheRx(int valueSize, int subjectSize, int fetcherSize,
            @NotNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(valueSize, subjectSize, dtoCacheUtilRx);
        this.cachedFetcherSubscriptions = new THLruCache<>(fetcherSize);
    }
    //</editor-fold>

    @NotNull abstract protected Observable<DTOType> fetch(@NotNull DTOKeyType key);

    @NotNull @Override
    protected BehaviorSubject<Pair<DTOKeyType, DTOType>> getOrCreateBehavior(@NotNull final DTOKeyType key)
    {
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = super.getOrCreateBehavior(key);
        if (cachedFetcherSubscriptions.get(key) == null)
        {
            fetch(key)
                    .map(new Func1<DTOType, Pair<DTOKeyType, DTOType>>()
                    {
                        @Override public Pair<DTOKeyType, DTOType> call(DTOType dtoType)
                        {
                            return Pair.create(key, dtoType);
                        }
                    })
                    .subscribe(new Observer<Pair<DTOKeyType,DTOType>>()
                    {
                        @Override public void onNext(Pair<DTOKeyType, DTOType> pair)
                        {
                            BaseFetchDTOCacheRx.this.onNext(pair.first, pair.second);
                        }

                        @Override public void onCompleted()
                        {
                            cachedFetcherSubscriptions.remove(key);
                        }

                        @Override public void onError(Throwable error)
                        {
                            cachedFetcherSubscriptions.remove(key);
                            BaseFetchDTOCacheRx.this.onError(key, error);
                        }
                    });
        }
        return cachedSubject;
    }

    public void onError(@NotNull DTOKeyType key, @NotNull Throwable error)
    {
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = getBehavior(key);
        if (cachedSubject != null)
        {
            cachedSubject.onError(error);
        }
    }

    @Override public void invalidate(@NotNull DTOKeyType key)
    {
        super.invalidate(key);
        Subscription subscription = cachedFetcherSubscriptions.remove(key);
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    @Override public void invalidateAll()
    {
        super.invalidateAll();
        Collection<Subscription> fetcherSubscriptions = cachedFetcherSubscriptions.snapshot().values();
        cachedFetcherSubscriptions.evictAll();
        for (Subscription subscription : fetcherSubscriptions)
        {
            subscription.unsubscribe();
        }
    }
}
