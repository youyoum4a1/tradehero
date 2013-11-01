package com.tradehero.th.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 7:21 PM Copyright (c) TradeHero */
public abstract class PagedItemListLoader<D extends ItemWithComparableId> extends AsyncTaskLoader<List<D>>
{
    private static final int DEFAULT_ITEM_PER_PAGE = 10;

    private int itemsPerPage = DEFAULT_ITEM_PER_PAGE;
    private D lastVisibleItem;
    private D firstVisibleItem;
    protected List<D> items;
    private LoadMode currentLoadMode = LoadMode.IDLE;

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

    protected boolean shouldReload()
    {
        return items == null || items.isEmpty();
    }

    public void loadNextPage()
    {
        if (currentLoadMode != LoadMode.IDLE)
        {
            onBusy();
            return;
        }
        currentLoadMode = LoadMode.NEXT;
        onLoadNextPage(getFirstVisibleItem());
    }

    protected void onBusy()
    {
        // do nothing intentionally
    }

    protected abstract void onLoadNextPage(D lastItemId);

    public void loadPreviousPage()
    {
        if (currentLoadMode != LoadMode.IDLE)
        {
            onBusy();
            return;
        }
        currentLoadMode = LoadMode.PREVIOUS;
        onLoadPreviousPage(getLastVisibleItem());
    }

    protected abstract void onLoadPreviousPage(D startItemId);

    private boolean isEmpty()
    {
        return items == null || items.isEmpty();
    }

    private D getFirstItem()
    {
        if (isEmpty())
        {
            return null;
        }
        return items.get(0);
    }

    private D getLastItem()
    {
        if (isEmpty())
        {
            return null;
        }
        return items.get(items.size() - 1);
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

    private enum LoadMode
    {
        IDLE, PREVIOUS, NEXT
    }
}
