package com.tradehero.th.rx.view.list;

import android.view.View;
import android.widget.AbsListView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import java.util.Map;
import java.util.WeakHashMap;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

class CachedOnItemClickedListeners
{
    private static final Map<View, CompositeOnItemClickListener> sCachedListeners = new WeakHashMap<>();

    public static CompositeOnItemClickListener getFromViewOrCreate(final AbsListView view)
    {
        final CompositeOnItemClickListener listener = getOrCreateListener(view);
        view.setOnItemClickListener(listener);
        return listener;
    }

    public static CompositeOnItemClickListener getFromViewOrCreate(final PullToRefreshAdapterViewBase view)
    {
        final CompositeOnItemClickListener listener = getOrCreateListener(view);
        view.setOnItemClickListener(listener);
        return listener;
    }

    public static CompositeOnItemClickListener getFromViewOrCreate(final StickyListHeadersListView view)
    {
        final CompositeOnItemClickListener listener = getOrCreateListener(view);
        view.setOnItemClickListener(listener);
        return listener;
    }

    private static CompositeOnItemClickListener getOrCreateListener(final View view)
    {
        final CompositeOnItemClickListener cached = sCachedListeners.get(view);

        if (cached != null)
        {
            return cached;
        }

        final CompositeOnItemClickListener listener = new CompositeOnItemClickListener();

        sCachedListeners.put(view, listener);

        return listener;
    }
}
