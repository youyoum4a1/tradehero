package com.tradehero.common.persistence;

import android.support.annotation.NonNull;
import android.util.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.Subscription;
import rx.observers.EmptyObserver;
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
            cachedFetcherSubscriptions.put(key, fetch(key)
                    .doOnUnsubscribe(() -> removeFetcher(key))
                    .subscribe(new EmptyObserver<DTOType>()
                    {
                        @Override public void onNext(DTOType value)
                        {
                            BaseFetchDTOCacheRx.this.onNext(key, value);
                        }

                        @Override public void onError(Throwable error)
                        {
                            BaseFetchDTOCacheRx.this.onError(key, error);
                        }
                    }));
        }
        return cachedObservable;
    }

    private void removeFetcher(@NonNull DTOKeyType key)
    {
        cachedFetcherSubscriptions.remove(key);
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
