package com.tradehero.common.widget;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import timber.log.Timber;

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

    @Override public void onScrollStateChanged(final AbsListView view, final int state)
    {
        int lastVisiblePosition = view.getLastVisiblePosition();
        int height = view.getHeight();
        int childCount = view.getChildCount();
        View child = view.getChildAt(childCount-1);

        //if (this.active && !this.nearEnd )
        //{
            if (this.active && state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastItemVisible) {
                raiseFlag();
            }
        //}

    }

    //mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);

    private boolean mLastItemVisible = false;
    @Override public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount)
    {
        if (totalItemCount > 0 && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 1/*this.visibleThreshold*/)) {
            mLastItemVisible = true;
        } else {
            mLastItemVisible = false;
        }

    }

//    @Override public void onScrollStateChanged(final AbsListView view, final int state)
//    {
//        int lastVisiblePosition = view.getLastVisiblePosition();
//        int height = view.getHeight();
//        try {
//            int childCount = view.getChildCount();
//            View child = view.getChildAt(childCount-1);
//
//            if(child != null && child.getBottom() >= height){
//                /**
//                 * Check that the scrolling has stopped, and that the last item is
//                 * visible.
//                 */
////                if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && null != mOnLastItemVisibleListener && mLastItemVisible) {
////                    mOnLastItemVisibleListener.onLastItemVisible();
////                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    @Override public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount)
//    {
//        if (this.active && !this.nearEnd && (totalItemCount - visibleItemCount) <= (firstVisibleItem + this.visibleThreshold))
//        {
//            //THLog.d(TAG, "onScroll first: " + firstVisibleItem + ", visiCount: " + visibleItemCount + ", totalCount: " + totalItemCount);
//            raiseFlag();
//        }
//    }
}
