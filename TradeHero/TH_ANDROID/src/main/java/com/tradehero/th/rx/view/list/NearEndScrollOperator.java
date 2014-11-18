package com.tradehero.th.rx.view.list;

import android.widget.AbsListView;
import android.widget.ListView;
import rx.Subscriber;
import rx.android.observables.Assertions;
import rx.functions.Func0;

final class NearEndScrollOperator<T> implements AbsListView.OnScrollListener
{
    private final Subscriber<? super T> subscriber;
    private final Func0<T> itemCreator;
    private boolean scrollStateChanged;
    private int mTotalHeadersAndFooters = -1;

    public NearEndScrollOperator(Subscriber<? super T> subscriber, Func0<T> itemCreator)
    {
        this.subscriber = subscriber;
        this.itemCreator = itemCreator;
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        scrollStateChanged = true;
    }

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        Assertions.assertUiThread();
        if (mTotalHeadersAndFooters == -1)
        {
            obtainHeadersAndFootersCount(view);
        }

        if (totalItemCount > mTotalHeadersAndFooters && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 1))
        {
            if (scrollStateChanged)
            {
                scrollStateChanged = false;
                subscriber.onNext(itemCreator.call());
            }
        }
    }

    private synchronized void obtainHeadersAndFootersCount(AbsListView view)
    {
        mTotalHeadersAndFooters = 0;
        if (view instanceof ListView)
        {
            ListView listView = (ListView) view;
            mTotalHeadersAndFooters = listView.getHeaderViewsCount() + listView.getFooterViewsCount();
        }
    }
}
