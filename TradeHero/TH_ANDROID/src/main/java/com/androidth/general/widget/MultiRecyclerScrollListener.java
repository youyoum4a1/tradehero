package com.androidth.general.widget;

import android.support.v7.widget.RecyclerView;
import com.androidth.general.common.utils.CollectionUtils;
import rx.functions.Action1;
import timber.log.Timber;

public class MultiRecyclerScrollListener extends RecyclerView.OnScrollListener
{
    private final RecyclerView.OnScrollListener[] onScrollListeners;

    public MultiRecyclerScrollListener(RecyclerView.OnScrollListener... onScrollListeners)
    {
        if (onScrollListeners.length == 0)
        {
            throw new IllegalArgumentException("MultiScrollListener needs at least 1 child listener");
        }
        this.onScrollListeners = onScrollListeners;
    }

    @Override public void onScrollStateChanged(final RecyclerView view, final int scrollState)
    {
        CollectionUtils.apply(onScrollListeners, new Action1<RecyclerView.OnScrollListener>()
        {
            @Override public void call(RecyclerView.OnScrollListener onScrollListener)
            {
                if (onScrollListener != null)
                {
                    onScrollListener.onScrollStateChanged(view, scrollState);
                }
            }
        });
    }

    @Override public void onScrolled(final RecyclerView view, final int dx, final int dy)
    {
        CollectionUtils.apply(onScrollListeners, new Action1<RecyclerView.OnScrollListener>()
        {
            @Override public void call(RecyclerView.OnScrollListener onScrollListener)
            {
                if (onScrollListener != null)
                {
                    try
                    {
                        onScrollListener.onScrolled(view, dx, dy);
                    } catch (Exception e)
                    {
                        //Catch the NPE https://www.pivotaltracker.com/n/projects/559137/stories/93074886
                        Timber.e(e, "Error");
                    }
                }
            }
        });
    }
}
