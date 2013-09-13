package com.tradehero.th.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 7:21 PM Copyright (c) TradeHero */
public abstract class PagedItemListLoader<D extends ItemWithComparableId> extends AsyncTaskLoader<List<D>>
{
    private static final int DEFAULT_ITEM_PER_PAGE = 10;

    protected int itemsPerPage = DEFAULT_ITEM_PER_PAGE;
    private D lastVisibleItem;
    private D firstVisibleItem;
    protected List<D> items;

    public PagedItemListLoader(Context context)
    {
        super(context);
        items = new LinkedList<>();
    }

    public void setItemsPerPage(int itemsPerPage)
    {
        this.itemsPerPage = itemsPerPage;
    }

    public int getItemsPerPage()
    {
        return itemsPerPage;
    }

    public List<D> getItems()
    {
        return items;
    }

    protected abstract boolean shouldReload();

    public void loadNextPage()
    {
        if (getFirstVisibleItem().compareTo(getFirstItem()) < 0)
        {
            // do nothing if next page is already loaded
            return;
        }
        onLoadNextPage(getLastVisibleItem());
    }

    private Object getFirstItem()
    {
        if (isEmpty())
        {
            return null;
        }
        return items.get(0);
    }

    protected abstract void onLoadNextPage(D lastItemId);

    private D getLastItem()
    {
        if (isEmpty())
        {
            return null;
        }
        return items.get(items.size() - 1);
    }

    private boolean isEmpty()
    {
        return items == null || items.isEmpty();
    }

    public void loadPreviousPage()
    {
        if (getLastVisibleItem().compareTo(getLastItem()) > 0)
        {
            // do nothing if next page is already loaded
            return;
        }
        onLoadPreviousPage(getFirstVisibleItem());
    }

    protected abstract void onLoadPreviousPage(D startItemId);

    @Override protected void onStartLoading()
    {
        if (items == null || shouldReload())
        {
            forceLoad();
        }
        else
        {
            deliverResult(items);
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
            super.deliverResult(data);
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
        items = null;
    }

    public D getLastVisibleItem()
    {
        return lastVisibleItem;
    }

    public void setLastVisibleItem(D lastVisibleItem)
    {
        this.lastVisibleItem = lastVisibleItem;
    }

    public D getFirstVisibleItem()
    {
        return firstVisibleItem;
    }

    public void setFirstVisibleItem(D firstVisibleItem)
    {
        this.firstVisibleItem = firstVisibleItem;
    }
}
