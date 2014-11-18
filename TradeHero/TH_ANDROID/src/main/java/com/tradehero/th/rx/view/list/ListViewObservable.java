package com.tradehero.th.rx.view.list;

import android.widget.AbsListView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

public class ListViewObservable
{
    /** Use SwipeRefreshView instead */
    @Deprecated
    public static <S extends AbsListView, T extends PullToRefreshAdapterViewBase<S>> Observable<ItemClickDTO> itemClicks(final T ptrListView)
    {
        return Observable.create(new OperatorPullToRefreshViewBaseClick<>(ptrListView));
    }

    public static <T> NearEndScrollOperator<T> createNearEndScrollOperator(Subscriber<T> subscriber, Func0<T> func)
    {
        return new NearEndScrollOperator<>(subscriber, func);
    }
}
