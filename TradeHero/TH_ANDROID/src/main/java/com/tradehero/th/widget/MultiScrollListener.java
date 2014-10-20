package com.tradehero.th.widget;

import android.widget.AbsListView;
import com.tradehero.common.utils.CollectionUtils;
import rx.functions.Action1;

public class MultiScrollListener implements AbsListView.OnScrollListener
{
    private final AbsListView.OnScrollListener[] onScrollListeners;

    public MultiScrollListener(AbsListView.OnScrollListener... onScrollListeners)
    {
        if (onScrollListeners.length == 0)
        {
            throw new IllegalArgumentException("MultiScrollListener needs at least 1 child listener");
        }
        this.onScrollListeners = onScrollListeners;
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        for (AbsListView.OnScrollListener onScrollListener: onScrollListeners)
        {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount)
    {
        CollectionUtils.apply(onScrollListeners, new Action1<AbsListView.OnScrollListener>()
        {
            @Override public void call(AbsListView.OnScrollListener onScrollListener)
            {
                if (onScrollListener != null)
                {
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        });
    }
}
