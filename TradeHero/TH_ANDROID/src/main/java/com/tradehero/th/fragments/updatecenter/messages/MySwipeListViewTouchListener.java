package com.tradehero.th.fragments.updatecenter.messages;

import android.view.View;
import android.widget.AbsListView;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.fortysevendeg.android.swipelistview.SwipeListViewTouchListener;
import java.util.List;

public class MySwipeListViewTouchListener extends SwipeListViewTouchListener
{

    AbsListView.OnScrollListener onScrollListener;

    /**
     * Constructor
     *
     * @param swipeListView SwipeListView
     * @param swipeFrontView front view Identifier
     * @param swipeBackView back view Identifier
     */
    public MySwipeListViewTouchListener(
            SwipeListView swipeListView, int swipeFrontView, int swipeBackView)
    {
        super(swipeListView, swipeFrontView, swipeBackView);
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener)
    {
        this.onScrollListener = onScrollListener;
    }

    @Override public void openAnimate(int position)
    {
        super.openAnimate(position);
    }

    @Override public boolean isSwipeEnabled()
    {
        return super.isSwipeEnabled();
    }

    @Override public int dismiss(int position)
    {
        return super.dismiss(position);
    }

    @Override public void unselectedChoiceStates()
    {
        super.unselectedChoiceStates();
    }

    @Override public boolean isChecked(int position)
    {
        return super.isChecked(position);
    }




    @Override public void reloadChoiceStateInView(View frontView, int position)
    {
        super.reloadChoiceStateInView(frontView, position);
    }

    @Override public int getCountSelected()
    {
        return super.getCountSelected();
    }

    @Override public List<Integer> getPositionsSelected()
    {
        return super.getPositionsSelected();
    }

    @Override public void returnOldActions()
    {
        super.returnOldActions();
    }

    @Override public void performDismiss(View dismissView, int dismissPosition,
            boolean doPendingDismiss)
    {
        super.performDismiss(dismissView, dismissPosition, doPendingDismiss);
    }

    @Override public void resetPendingDismisses()
    {
        super.resetPendingDismisses();
    }

    @Override public void handlerPendingDismisses(int originalHeight)
    {
        super.handlerPendingDismisses(originalHeight);
    }

    @Override public void closeAnimate(int position)
    {
        super.closeAnimate(position);
    }

    @Override public AbsListView.OnScrollListener makeScrollListener()
    {
        final AbsListView.OnScrollListener originalOnScrollListener = super.makeScrollListener();

        return new AbsListView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState)
            {
                originalOnScrollListener.onScrollStateChanged(absListView, scrollState);
                if (onScrollListener != null)
                {
                    onScrollListener.onScrollStateChanged(absListView, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount)
            {
                originalOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
                if (onScrollListener != null)
                {
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                            totalItemCount);
                }
            }
        };
    }

    @Override public void setSwipeDrawableChecked(int swipeDrawableChecked)
    {
        super.setSwipeDrawableChecked(swipeDrawableChecked);
    }

    @Override public void setSwipeDrawableUnchecked(int swipeDrawableUnchecked)
    {
        super.setSwipeDrawableUnchecked(swipeDrawableUnchecked);
    }
}