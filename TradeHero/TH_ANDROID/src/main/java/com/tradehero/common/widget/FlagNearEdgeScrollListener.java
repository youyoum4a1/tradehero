package com.tradehero.common.widget;

import android.widget.AbsListView;

/**
 * It raises the flag when near the end. - Extend raiseEndFlag to find when. - Lower the flag when the
 * end has moved away.
 */
abstract public class FlagNearEdgeScrollListener implements AbsListView.OnScrollListener
{
    public static final int DEFAULT_VISIBLE_THRESHOLD = 5;

    private int visibleThreshold;
    private boolean nearStart = false;
    private boolean nearStartActive = false;
    private boolean mFirstItemVisible = false;
    private boolean nearEnd = false;
    private boolean nearEndActive = false;
    private boolean mLastItemVisible = false;

    //<editor-fold desc="Constructors">
    public FlagNearEdgeScrollListener()
    {
        this(DEFAULT_VISIBLE_THRESHOLD);
    }

    public FlagNearEdgeScrollListener(final int visibleThreshold)
    {
        this.visibleThreshold = visibleThreshold;
    }
    //</editor-fold>

    public boolean isNearStart()
    {
        return this.nearStart;
    }

    public boolean isNearEnd()
    {
        return this.nearEnd;
    }

    public void lowerStartFlag()
    {
        this.nearStart = false;
    }

    public void raiseStartFlag()
    {
        this.nearStart = true;
    }

    public void lowerEndFlag()
    {
        this.nearEnd = false;
    }

    public void raiseEndFlag()
    {
        this.nearEnd = true;
    }

    public boolean isNearStartActive()
    {
        return this.nearStartActive;
    }

    public boolean isNearEndActive()
    {
        return this.nearEndActive;
    }

    public void activateStart()
    {
        this.nearStartActive = true;
    }

    public void deactivateStart()
    {
        this.nearStartActive = false;
    }

    public void activateEnd()
    {
        this.nearEndActive = true;
    }

    public void deactivateEnd()
    {
        this.nearEndActive = false;
    }

    @Override public void onScrollStateChanged(final AbsListView view, final int state)
    {
        if (this.nearEndActive
                && state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && mLastItemVisible)
        {
            raiseEndFlag();
        }
        if (this.nearStartActive
                && state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && mFirstItemVisible)
        {
            raiseStartFlag();
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
        if (firstVisibleItem == 0)
        {
            mFirstItemVisible = true;
        }
        else
        {
            mFirstItemVisible = false;
        }
    }
}
