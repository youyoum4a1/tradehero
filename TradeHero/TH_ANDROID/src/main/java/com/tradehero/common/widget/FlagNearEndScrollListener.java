package com.tradehero.common.widget;

import android.widget.AbsListView;

/**
 * It raises the flag when near the end.
 * - Extend raiseFlag to find when.
 * - Lower the flag when the end has moved away.
 * Created by xavier on 12/11/13.
 */
abstract public class FlagNearEndScrollListener implements AbsListView.OnScrollListener
{
    public static final String TAG = FlagNearEndScrollListener.class.getSimpleName();

    public static final int DEFAULT_VISIBLE_THRESHOLD = 5;

    private int visibleThreshold;
    private boolean nearEnd = false;
    private boolean active = false;

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
        //THLog.d(TAG, "Lowering flag");
        this.nearEnd = false;
    }

    public void raiseFlag()
    {
        //THLog.d(TAG, "Raising flag");
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

    @Override public void onScrollStateChanged(final AbsListView view, final int scrollState)
    {
    }

    @Override public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount)
    {
        if (this.active && !this.nearEnd && (totalItemCount - visibleItemCount) <= (firstVisibleItem + this.visibleThreshold))
        {
            //THLog.d(TAG, "onScroll first: " + firstVisibleItem + ", visiCount: " + visibleItemCount + ", totalCount: " + totalItemCount);
            raiseFlag();
        }
    }
}
