package com.tradehero.th.widget;

import android.widget.AbsListView;

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

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        for (AbsListView.OnScrollListener onScrollListener: onScrollListeners)
        {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
