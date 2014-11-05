package com.tradehero.common.persistence;

import android.util.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import android.support.annotation.NonNull;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

abstract public class BaseFetchDTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends BaseDTOCacheRx<DTOKeyType, DTOType>
{
    @NonNull final private Map<DTOKeyType, Subscription> cachedFetcherSubscriptions;

    //<editor-fold desc="Constructors">
    protected BaseFetchDTOCacheRx(int valueSize, int subjectSize, int fetcherSize,
            @NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(valueSize, subjectSize, dtoCacheUtilRx);
        this.cachedFetcherSubscriptions = new HashMap<>();
    }
    //</editor-fold>

    @NonNull abstract protected Observable<DTOType> fetch(@NonNull DTOKeyType key);

    @NonNull @Override
    protected Observable<Pair<DTOKeyType, DTOType>> getOrCreateObservable(@NonNull final DTOKeyType key)
    {
        Observable<Pair<DTOKeyType, DTOType>> cachedObservable = super.getOrCreateObservable(key);
        if (cachedFetcherSubscriptions.get(key) == null)
        {
            fetch(key)
                    .map(dtoType -> Pair.create(key, dtoType))
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
        return cachedObservable;
    }

    public void onError(@NonNull DTOKeyType key, @NonNull Throwable error)
    {
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = getBehavior(key);
        if (cachedSubject != null)
        {
            cachedSubject.onError(error);
        }
    }

    @Override public void invalidate(@NonNull DTOKeyType key)
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
        Collection<Subscription> fetcherSubscriptions = cachedFetcherSubscriptions.values();
        cachedFetcherSubscriptions.clear();
        for (Subscription subscription : fetcherSubscriptions)
        {
            subscription.unsubscribe();
        }
    }
}
