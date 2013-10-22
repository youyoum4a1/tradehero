package com.tradehero.th.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.loaders.ItemWithComparableId;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/13/13 Time: 11:23 AM Copyright (c) TradeHero */
public class PullToUpdateFromLoaderListView extends PullToRefreshListView
{
    private Loader<List<ItemWithComparableId>> loader;

    public PullToUpdateFromLoaderListView(Context context)
    {
        super(context);
    }

    public PullToUpdateFromLoaderListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PullToUpdateFromLoaderListView(Context context, Mode mode)
    {
        super(context, mode);
    }

    public PullToUpdateFromLoaderListView(Context context, Mode mode, AnimationStyle style)
    {
        super(context, mode, style);
    }

    private void enableAutoFocus()
    {
        setOnScrollListener(new AbsListView.OnScrollListener()
        {
            private View lastVisibleView = null;

            @Override public void onScrollStateChanged(AbsListView absListView, int state)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                View middleView = view.getChildAt(firstVisibleItem + visibleItemCount / 2);
                if (middleView == lastVisibleView)
                {
                    return;
                }
                if (middleView != null)
                {
                    middleView.setBackgroundColor(Color.RED);
                    if (lastVisibleView != null)
                    {
                        lastVisibleView.setBackgroundColor(getResources().getColor(R.color.timeline_list_item_background));
                    }
                    lastVisibleView = middleView;
                }
            }
        });
    }
}
