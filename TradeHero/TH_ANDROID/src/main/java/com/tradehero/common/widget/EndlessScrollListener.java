package com.tradehero.common.widget;

import android.widget.AbsListView;

/**
 * Created by xavier on 12/5/13.
 */
abstract public class EndlessScrollListener implements AbsListView.OnScrollListener
{
    public static final String TAG = EndlessScrollListener.class.getSimpleName();

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;

    //<editor-fold desc="Constructors">
    public EndlessScrollListener()
    {
    }

    public EndlessScrollListener(int visibleThreshold)
    {
        this.visibleThreshold = visibleThreshold;
    }
    //</editor-fold>

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (loading)
        {
            if (totalItemCount > previousTotal)
            {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold))
        {
            loadPage(currentPage + 1);
            loading = true;
        }
    }

    abstract protected void loadPage(int page);

    @Override public void onScrollStateChanged(AbsListView view, int scrollState)
    {
    }
}
