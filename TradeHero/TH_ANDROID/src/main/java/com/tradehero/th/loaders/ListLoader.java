package com.tradehero.th.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import java.util.LinkedList;
import java.util.List;

public abstract class ListLoader<D> extends AsyncTaskLoader<List<D>>
{
    @NonNull protected final List<D> items;

    //<editor-fold desc="Constructors">
    public ListLoader(Context context)
    {
        super(context);
        items = new LinkedList<>();
    }
    //</editor-fold>

    @NonNull public List<D> getItems()
    {
        return items;
    }

    protected void onBusy()
    {
        // do nothing intentionally
    }

    protected boolean shouldReload()
    {
        return items.isEmpty();
    }

    private boolean isEmpty()
    {
        return items.isEmpty();
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

    @Override public void onCanceled(List<D> data)
    {
        super.onCanceled(data);

        releaseResources(data);
    }

    protected void releaseResources(List<D> data)
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
