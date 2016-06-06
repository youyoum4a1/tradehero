package com.androidth.general.common.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * It raises the flag when near the end. - Extend raiseEndFlag to find when. - Lower the flag when the
 * end has moved away.
 */
abstract public class FlagNearEdgeRecyclerScrollListener extends RecyclerView.OnScrollListener
{
    public static final int DEFAULT_VISIBLE_THRESHOLD = 5;

    private final int visibleThreshold;
    private boolean nearStart = false;
    private boolean nearStartActive = false;
    private boolean mFirstItemVisible = false;
    private boolean nearEnd = false;
    private boolean nearEndActive = false;
    private boolean mLastItemVisible = false;

    //<editor-fold desc="Constructors">
    public FlagNearEdgeRecyclerScrollListener()
    {
        this(DEFAULT_VISIBLE_THRESHOLD);
    }

    public FlagNearEdgeRecyclerScrollListener(final int visibleThreshold)
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

    @Override public void onScrollStateChanged(final RecyclerView view, final int state)
    {
        if (this.nearEndActive
                && state == RecyclerView.SCROLL_STATE_IDLE
                && mLastItemVisible)
        {
            raiseEndFlag();
        }
        if (this.nearStartActive
                && state == RecyclerView.SCROLL_STATE_IDLE
                && mFirstItemVisible)
        {
            raiseStartFlag();
        }
    }

    @Override public void onScrolled(final RecyclerView view, int dx, int dy)
    {
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) view.getLayoutManager();
        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

        mLastItemVisible = totalItemCount > 0 && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 1);
        mFirstItemVisible = firstVisibleItem == 0;
    }
}
