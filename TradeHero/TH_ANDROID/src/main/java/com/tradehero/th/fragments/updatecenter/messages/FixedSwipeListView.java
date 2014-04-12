package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.fortysevendeg.android.swipelistview.SwipeListViewTouchListener;
import java.lang.reflect.Field;
import timber.log.Timber;

/**
 * Created by wangliang on 14-4-12.
 */
public class FixedSwipeListView extends SwipeListView
{
    public FixedSwipeListView(Context context, int swipeBackView, int swipeFrontView)
    {
        super(context, swipeBackView, swipeFrontView);
    }

    public FixedSwipeListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FixedSwipeListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /**
     * Internal touch listener
     */
    private MySwipeListViewTouchListener touchListener;

    ///**
    // * Init ListView
    // *
    // * @param attrs AttributeSet
    // */
    //private void init(AttributeSet attrs) {
    //
    //    int swipeMode = SWIPE_MODE_BOTH;
    //    boolean swipeOpenOnLongPress = true;
    //    boolean swipeCloseAllItemsWhenMoveList = true;
    //    long swipeAnimationTime = 0;
    //    float swipeOffsetLeft = 0;
    //    float swipeOffsetRight = 0;
    //    int swipeDrawableChecked = 0;
    //    int swipeDrawableUnchecked = 0;
    //
    //    int swipeActionLeft = SWIPE_ACTION_REVEAL;
    //    int swipeActionRight = SWIPE_ACTION_REVEAL;
    //
    //    int swipeBackView = 0;
    //    int swipeFrontView =0;
    //
    //    if (attrs != null) {
    //        TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeListView);
    //        swipeMode = styled.getInt(R.styleable.SwipeListView_swipeMode, SWIPE_MODE_BOTH);
    //        swipeActionLeft = styled.getInt(R.styleable.SwipeListView_swipeActionLeft, SWIPE_ACTION_REVEAL);
    //        swipeActionRight = styled.getInt(R.styleable.SwipeListView_swipeActionRight, SWIPE_ACTION_REVEAL);
    //        swipeOffsetLeft = styled.getDimension(R.styleable.SwipeListView_swipeOffsetLeft, 0);
    //        swipeOffsetRight = styled.getDimension(R.styleable.SwipeListView_swipeOffsetRight, 0);
    //        swipeOpenOnLongPress = styled.getBoolean(R.styleable.SwipeListView_swipeOpenOnLongPress, true);
    //        swipeAnimationTime = styled.getInteger(R.styleable.SwipeListView_swipeAnimationTime, 0);
    //        swipeCloseAllItemsWhenMoveList = styled.getBoolean(R.styleable.SwipeListView_swipeCloseAllItemsWhenMoveList, true);
    //        swipeDrawableChecked = styled.getResourceId(R.styleable.SwipeListView_swipeDrawableChecked, 0);
    //        swipeDrawableUnchecked = styled.getResourceId(R.styleable.SwipeListView_swipeDrawableUnchecked, 0);
    //        swipeFrontView = styled.getResourceId(R.styleable.SwipeListView_swipeFrontView, 0);
    //        swipeBackView = styled.getResourceId(R.styleable.SwipeListView_swipeBackView, 0);
    //    }
    //
    //    if (swipeFrontView == 0 || swipeBackView == 0) {
    //        swipeFrontView = getContext().getResources().getIdentifier(SWIPE_DEFAULT_FRONT_VIEW, "id", getContext().getPackageName());
    //        swipeBackView = getContext().getResources().getIdentifier(SWIPE_DEFAULT_BACK_VIEW, "id", getContext().getPackageName());
    //
    //        if (swipeFrontView == 0 || swipeBackView == 0) {
    //            throw new RuntimeException(String.format("You forgot the attributes swipeFrontView or swipeBackView. You can add this attributes or use '%s' and '%s' identifiers", SWIPE_DEFAULT_FRONT_VIEW, SWIPE_DEFAULT_BACK_VIEW));
    //        }
    //    }
    //
    //    final ViewConfiguration configuration = ViewConfiguration.get(getContext());
    //    //ViewConfiguration ViewConfigurationCompat;
    //    //touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    //    touchListener = new MySwipeListViewTouchListener(this, swipeFrontView, swipeBackView);
    //    if (swipeAnimationTime > 0) {
    //        touchListener.setAnimationTime(swipeAnimationTime);
    //    }
    //    touchListener.setRightOffset(swipeOffsetRight);
    //    touchListener.setLeftOffset(swipeOffsetLeft);
    //    touchListener.setSwipeActionLeft(swipeActionLeft);
    //    touchListener.setSwipeActionRight(swipeActionRight);
    //    touchListener.setSwipeMode(swipeMode);
    //    touchListener.setSwipeClosesAllItemsWhenListMoves(swipeCloseAllItemsWhenMoveList);
    //    touchListener.setSwipeOpenOnLongPress(swipeOpenOnLongPress);
    //    touchListener.setSwipeDrawableChecked(swipeDrawableChecked);
    //    touchListener.setSwipeDrawableUnchecked(swipeDrawableUnchecked);
    //    setOnTouchListener(touchListener);
    //    //touchListener.setOnScrollListener();
    //
    //    setOnScrollListener(touchListener.makeScrollListener());
    //}

    private AbsListView.OnScrollListener innerOnScrollListener;

    @Override public void setOnScrollListener(final OnScrollListener onScrollListener)
    {
        try
        {
            Field f = SwipeListView.class.getDeclaredField("touchListener");
            f.setAccessible(true);
            SwipeListViewTouchListener touchListener = (SwipeListViewTouchListener) f.get(this);
            innerOnScrollListener = touchListener.makeScrollListener();
        } catch (Exception e)
        {
            Timber.e("FixedSwipeListView Error", e);
        }

        AbsListView.OnScrollListener finalOnScrollListener = new AbsListView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState)
            {

                if (innerOnScrollListener != null)
                {
                    innerOnScrollListener.onScrollStateChanged(absListView, scrollState);
                }
                if (onScrollListener != null)
                {
                    onScrollListener.onScrollStateChanged(absListView, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount)
            {
                if (innerOnScrollListener != null)
                {
                    innerOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                            totalItemCount);
                }

                if (onScrollListener != null)
                {
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                            totalItemCount);
                }
            }
        };

        super.setOnScrollListener(finalOnScrollListener);
    }
}
