package com.tradehero.th.loaders;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 7:21 PM Copyright (c) TradeHero */
public abstract class ItemListLoader<D> extends AsyncTaskLoader<D>
{

    private int itemPerPage;
    private D items = null;

    public ItemListLoader(Context context)
    {
        super(context);
    }

    public void setItemPerPage(int itemPerPage)
    {
        this.itemPerPage = itemPerPage;
    }

    public int getItemPerPage()
    {
        return itemPerPage;
    }


    @Override protected void onStartLoading()
    {
        // if we have result available currently, deliver it
        if (items != null)
        {
            deliverResult(items);
        }

        if (items == null || shouldReload())
        {
            forceLoad();
        }
    }

    protected abstract boolean shouldReload();

    @Override protected void onStopLoading()
    {
        super.onStopLoading();
    }

    @Override public void onCanceled(D data)
    {
        super.onCanceled(data);
    }

    @Override public void deliverResult(D data)
    {
        super.deliverResult(data);
    }

    @Override protected void onReset()
    {
        super.onReset();
    }
}
