package com.tradehero.th.rx;

import android.util.Pair;
import com.tradehero.th.api.pagination.RangeDTO;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Created by thonguyen on 23/10/14.
 */
public class PaginationObservable
{
    private static Map<Object, Observable<?>> cachedRequests = new WeakHashMap<>();
    private static Map<Object, RangeDTO> currentRanges = new WeakHashMap<>();

    /**
     * TODO compare RangeDTO with the previous for better merging, maybe we don't need to sort ...
     * Transform a list into a Observable of an paginated list
     * @param rangeObservable observable of a range
     * @return observable of paginatable list of items
     */
    public static <K, T extends Comparable<T>> Observable<List<T>> create(
            final K key,
            final Observable<RangeDTO> rangeObservable,
            final Func1<RangeDTO, Observable<List<T>>> fetchFunc)
    {
        final PublishSubject<T> proxy = PublishSubject.create();
        proxy.subscribe(new Observer<T>()
        {
            @Override public void onCompleted()
            {
                cachedRequests.put(key, proxy);
            }

            @Override public void onError(Throwable e)
            {
            }

            @Override public void onNext(T ts)
            {
            }
        });
        rangeObservable
                .flatMap(new Func1<RangeDTO, Observable<List<T>>>()
                {
                    @Override public Observable<List<T>> call(final RangeDTO rangeDTO)
                    {
                        return fetchFunc.call(rangeDTO);
                    }
                })
                .flatMap(new Func1<List<T>, Observable<T>>()
                {
                    @Override public Observable<T> call(List<T> ts)
                    {
                        return Observable.from(ts);
                    }
                })
                .compose(new Observable.Transformer<T, T>()
                {
                    @Override public Observable<T> call(Observable<? extends T> observable)
                    {
                        Observable<T> cachedRequest = getCachedRequest(key);
                        return cachedRequest.mergeWith(observable);
                    }
                })
                .subscribe(proxy);
        return proxy.toSortedList(new Func2<T, T, Integer>()
        {
            @Override public Integer call(T t, T t2)
            {
                return -t.compareTo(t2);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <K, T> Observable<T> getCachedRequest(K key)
    {
        Observable<T> result = (Observable<T>) cachedRequests.get(key);
        if (result == null)
        {
            result = Observable.empty();
        }
        return result;
    }
}
