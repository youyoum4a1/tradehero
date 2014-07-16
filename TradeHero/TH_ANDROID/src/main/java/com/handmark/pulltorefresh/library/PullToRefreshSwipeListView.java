package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;

public class PullToRefreshSwipeListView extends PullToRefreshListViewBase<SwipeListView>
{
    //<editor-fold desc="Constructors">
    public PullToRefreshSwipeListView(Context context)
    {
        super(context);
    }

    public PullToRefreshSwipeListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PullToRefreshSwipeListView(Context context, Mode mode)
    {
        super(context, mode);
    }

    public PullToRefreshSwipeListView(Context context, Mode mode, AnimationStyle style)
    {
        super(context, mode, style);
    }
    //</editor-fold>

    protected SwipeListView createListView(Context context, AttributeSet attrs)
    {
        final SwipeListView lv;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD)
        {
            lv = new InternalSwipeListViewSDK9(context, attrs);
        }
        else
        {
            lv = new InternalSwipeListView(context, attrs);
        }
        return lv;
    }

    @TargetApi(9)
    final class InternalSwipeListViewSDK9 extends InternalSwipeListView
    {
        public InternalSwipeListViewSDK9(Context context, AttributeSet attrs)
        {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                int scrollRangeX,
                int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
        {

            final boolean returnValue =
                    super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                            scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshSwipeListView.this, deltaX, scrollX, deltaY,
                    scrollY, isTouchEvent);

            return returnValue;
        }
    }

    protected class InternalSwipeListView extends SwipeListView implements EmptyViewMethodAccessor
    {
        private boolean mAddedLvFooter = false;

        public InternalSwipeListView(Context context, AttributeSet attrs)
        {
            super(context, attrs);
        }

        @Override
        protected void dispatchDraw(Canvas canvas)
        {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try
            {
                super.dispatchDraw(canvas);
            }
            catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev)
        {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try
            {
                return super.dispatchTouchEvent(ev);
            }
            catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void setAdapter(ListAdapter adapter)
        {
            // Add the Footer View at the last possible moment
            if (null != mLvFooterLoadingFrame && !mAddedLvFooter)
            {
                addFooterView(mLvFooterLoadingFrame, null, false);
                mAddedLvFooter = true;
            }

            super.setAdapter(adapter);
        }

        @Override
        public void setEmptyView(View emptyView)
        {
            PullToRefreshSwipeListView.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView)
        {
            super.setEmptyView(emptyView);
        }
    }
}