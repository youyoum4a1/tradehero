package com.tradehero.common.widget;

import android.widget.AbsListView;
import timber.log.Timber;


abstract public class EndlessScrollListener implements AbsListView.OnScrollListener
{
    public static final int DEFAULT_VISIBLE_THRESHOLD = 5;

    private int visibleThreshold;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;

    //<editor-fold desc="Constructors">
    public EndlessScrollListener()
    {
        this(DEFAULT_VISIBLE_THRESHOLD);
    }

    public EndlessScrollListener(int visibleThreshold)
    {
        this.visibleThreshold = visibleThreshold;
        resetCounters();
    }
    //</editor-fold>

    public void resetCounters()
    {
        currentPage = 0;
        previousTotal = 0;
        loading = true;
    }

    public int getVisibleThreshold()
    {
        return visibleThreshold;
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    public int getPreviousTotal()
    {
        return previousTotal;
    }

    public boolean isLoading()
    {
        return loading;
    }

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        Timber.d("onScroll first: %d, visiCount: %d, totalCount: %d", firstVisibleItem, visibleItemCount, totalItemCount);
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
