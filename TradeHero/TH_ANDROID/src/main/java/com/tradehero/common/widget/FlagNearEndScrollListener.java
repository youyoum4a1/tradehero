package com.tradehero.common.widget;

import android.widget.AbsListView;

/**
 * It raises the flag when near the end. - Extend raiseFlag to find when. - Lower the flag when the
 * end has moved away.
 */
abstract public class FlagNearEndScrollListener implements AbsListView.OnScrollListener
{
    public static final int DEFAULT_VISIBLE_THRESHOLD = 5;

    private int visibleThreshold;
    private boolean nearEnd = false;
    private boolean active = false;
    private boolean mLastItemVisible = false;

    //<editor-fold desc="Constructors">
    public FlagNearEndScrollListener()
    {
        this(DEFAULT_VISIBLE_THRESHOLD);
    }

    public FlagNearEndScrollListener(final int visibleThreshold)
    {
        this.visibleThreshold = visibleThreshold;
    }
    //</editor-fold>

    public boolean isNearEnd()
    {
        return this.nearEnd;
    }

    public void lowerFlag()
    {
        this.nearEnd = false;
    }

    public void raiseFlag()
    {
        this.nearEnd = true;
    }

    public boolean isActive()
    {
        return this.active;
    }

    public void activate()
    {
        this.active = true;
    }

    public void deactivate()
    {
        this.active = false;
    }

    @Override public void onScrollStateChanged(final AbsListView view, final int state)
    {
        if (this.active
                && state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && mLastItemVisible)
        {
            raiseFlag();
        }
    }

    @Override public void onScroll(final AbsListView view, final int firstVisibleItem,
            final int visibleItemCount, final int totalItemCount)
    {
        if (totalItemCount > 0 && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 1))
        {
            mLastItemVisible = true;
        }
        else
        {
            mLastItemVisible = false;
        }
    }
}
