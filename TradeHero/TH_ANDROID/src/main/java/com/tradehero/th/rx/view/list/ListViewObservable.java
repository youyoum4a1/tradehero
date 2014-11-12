package com.tradehero.th.rx.view.list;

import android.widget.AbsListView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
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

    public static final boolean REFRESH_ON_LAST_ELEMENT = true;
    public static final boolean NO_REFRESH_ON_LAST_ELEMENT = false;

    /** Deprecated since we are replacing PullToRefresh library by SwipeRefreshLayout */
    @Deprecated
    public static <S extends AbsListView, T extends PullToRefreshAdapterViewBase<S>>
    Observable.OnSubscribe<PullToRefreshBase.Mode> refreshOperator(final T ptrListView, boolean refreshOnLastElement)
    {
        return subscriber -> {
            ptrListView.setOnRefreshListener(ptrView -> subscriber.onNext(ptrView.getCurrentMode()));
            ptrListView.setOnLastItemVisibleListener(() -> {
                if (refreshOnLastElement
                        && !ptrListView.isRefreshing()
                        && ptrListView.getRefreshableView() != null
                        && ptrListView.getRefreshableView().getAdapter() != null
                        && !ptrListView.getRefreshableView().getAdapter().isEmpty())
                {
                    subscriber.onNext(PullToRefreshBase.Mode.PULL_FROM_END);
                }
            });
        };
    }
}
