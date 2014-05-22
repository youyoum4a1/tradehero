package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.fortysevendeg.android.swipelistview.SwipeListViewTouchListener;
import java.lang.reflect.Field;
import timber.log.Timber;

public class InterceptedScrollSwipeListView extends SwipeListView
{
    //<editor-fold desc="Constructors">
    public InterceptedScrollSwipeListView(Context context, int swipeBackView, int swipeFrontView)
    {
        super(context, swipeBackView, swipeFrontView);
    }

    public InterceptedScrollSwipeListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public InterceptedScrollSwipeListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    private AbsListView.OnScrollListener parentSwipeListViewOnScrollListener;

    protected SwipeListViewTouchListener getTouchListenerByReflection()
    {
        SwipeListViewTouchListener touchListener = null;
        try
        {
            Field f = SwipeListView.class.getDeclaredField("touchListener");
            f.setAccessible(true);
            touchListener = (SwipeListViewTouchListener) f.get(this);
        }
        catch (NoSuchFieldException|IllegalAccessException e)
        {
            Timber.e(e, "Failed to getTouchListenerByReflection ");
        }
        return touchListener;
    }

    @Override public void setOnScrollListener(final OnScrollListener onScrollListener)
    {
        parentSwipeListViewOnScrollListener = getTouchListenerByReflection().makeScrollListener();
        super.setOnScrollListener(createInterceptedOnScrollListener(onScrollListener));
    }

    protected AbsListView.OnScrollListener createInterceptedOnScrollListener(OnScrollListener ostensibleOnScrollListener)
    {
        return new InterceptedOnScrollListener(ostensibleOnScrollListener);
    }

    protected class InterceptedOnScrollListener implements AbsListView.OnScrollListener
    {
        protected OnScrollListener ostensibleOnScrollListener;

        public InterceptedOnScrollListener(OnScrollListener ostensibleOnScrollListener)
        {
            this.ostensibleOnScrollListener = ostensibleOnScrollListener;
        }

        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState)
        {
            parentSwipeListViewOnScrollListener.onScrollStateChanged(absListView, scrollState);
            ostensibleOnScrollListener.onScrollStateChanged(absListView, scrollState);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount)
        {
            parentSwipeListViewOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
            ostensibleOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }
}
