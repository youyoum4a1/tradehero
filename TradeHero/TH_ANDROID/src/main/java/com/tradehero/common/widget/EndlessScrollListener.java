package com.tradehero.common.widget;

import android.widget.AbsListView;

/**
 * Created by xavier on 12/5/13.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener
{
    public static final String TAG = EndlessScrollListener.class.getSimpleName();

    protected void loadPage(int page) {}

    @Override public void onScrollStateChanged(AbsListView view, int scrollState)
    {

    }

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {

    }
}
