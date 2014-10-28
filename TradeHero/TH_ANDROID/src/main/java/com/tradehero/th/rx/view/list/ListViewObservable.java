package com.tradehero.th.rx.view.list;

import android.widget.AbsListView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import rx.Observable;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ListViewObservable
{
    public static <T extends AbsListView> Observable<ItemClickDTO> itemClicks(final T absListView)
    {
        return Observable.create(new OperatorAbsListViewClick<>(absListView));
    }

    public static <S extends AbsListView, T extends PullToRefreshAdapterViewBase<S>> Observable<ItemClickDTO> itemClicks(final T ptrListView)
    {
        return Observable.create(new OperatorPullToRefreshViewBaseClick<>(ptrListView));
    }

    public static <T extends StickyListHeadersListView> Observable<ItemClickDTO> itemClicks(final T absListView)
    {
        return Observable.create(new OperatorStickyListHeadersListViewClick<>(absListView));
    }
}
