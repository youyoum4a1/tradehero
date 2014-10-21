package com.tradehero.th.rx;

import com.tradehero.th.api.users.UserBaseKey;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.security.auth.Subject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.ReplaySubject;

/**
 * Mimic LoaderManager, in a reactive way
 */
@Singleton
public class RxLoaderManager
{
    private final Map<Object, BehaviorSubject<?>> cachedRequests = new HashMap<>();

    @Inject public RxLoaderManager()
    {}

    /**
     * Create or get the cached loader/request
     * @param key for identify the loader, similar to `id` in `initLoader` method
     * @param task the worker whose job is to get the data
     * @return new or cached loader/request
     */
    public final <K, V> Observable<V> create(final K key, Observable<V> task)
    {
        // get the cached request if presence
        BehaviorSubject<V> cachedRequest = getCachedRequest(key);
        if (cachedRequest != null)
        {
            return cachedRequest;
        }

        // cache the request
        cachedRequest = BehaviorSubject.create();
        cachedRequests.put(key, cachedRequest);

        // invalidate request cache on complete/error
        cachedRequest.subscribe(new Observer<V>()
        {
            @Override public void onCompleted()
            {
                cachedRequests.remove(key);
            }

            @Override public void onError(Throwable e)
            {
                cachedRequests.remove(key);
            }

            @Override public void onNext(V v)
            {
                // nothing
            }
        });

        // forward data to the return observable
        task.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cachedRequest);
        return cachedRequest;
    }

    public final void remove(Object key)
    {
        cachedRequests.remove(key);
    }

    @SuppressWarnings("unchecked")
    private <K, V> BehaviorSubject<V> getCachedRequest(K key)
    {
        return (BehaviorSubject<V>) cachedRequests.get(key);
    }
}
