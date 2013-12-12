package com.tradehero.th.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 7:21 PM Copyright (c) TradeHero */
public abstract class ListLoader<D extends ItemWithComparableId> extends AsyncTaskLoader<List<D>>
{
    protected final List<D> items;
    private LoadMode currentLoadMode = LoadMode.IDLE;

    public ListLoader(Context context)
    {
        super(context);
        items = new LinkedList<>();
    }

    public List<D> getItems()
    {
        return items;
    }

    protected void onBusy()
    {
        // do nothing intentionally
    }

    protected boolean shouldReload()
    {
        return items == null || items.isEmpty();
    }

    private boolean isEmpty()
    {
        return items == null || items.isEmpty();
    }

    @Override protected void onStartLoading()
    {
        if (shouldReload())
        {
            forceLoad();
        }
        else
        {
            // Fix: do not start deliver old data anymore
            // TODO mark it as old items and deliver it
            //deliverResult(items);
            deliverResult(null);
        }
    }

    @Override protected void onStopLoading()
    {
        super.onStopLoading();
    }

    @Override public void onCanceled(List<D> data)
    {
        super.onCanceled(data);

        releaseResources(data);
    }

    @Override public void deliverResult(List<D> data)
    {
        if (isReset())
        {
            releaseResources(data);
            return;
        }

        if (isStarted())
        {
            if (data != null)
            {
                switch (currentLoadMode)
                {
                    case IDLE:
                    case NEXT:
                        items.addAll(0, data);
                        break;
                    case PREVIOUS:
                        items.addAll(data);
                        break;
                }
            }
            super.deliverResult(data);
            currentLoadMode = LoadMode.IDLE;
        }
    }

    private void releaseResources(List<D> data)
    {
        if (data != null)
        {
            data.clear();
        }
    }

    @Override protected void onReset()
    {
        onStopLoading();
        releaseResources(items);
    }

    public int getCount()
    {
        return getItems().size();
    }
}
