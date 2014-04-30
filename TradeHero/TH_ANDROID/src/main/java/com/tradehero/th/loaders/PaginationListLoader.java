package com.tradehero.th.loaders;

import android.content.Context;
import java.util.List;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/10/13 Time: 5:02 PM Copyright (c) TradeHero
 */
public abstract class PaginationListLoader<D> extends ListLoader<D>
{
    private static final int DEFAULT_ITEM_PER_PAGE = 10;
    private int itemsPerPage = DEFAULT_ITEM_PER_PAGE;

    private LoadMode loadMode = LoadMode.IDLE;

    public PaginationListLoader(Context context)
    {
        super(context);
    }

    public void setPerPage(int itemsPerPage)
    {
        this.itemsPerPage = itemsPerPage;
    }

    public int getPerPage()
    {
        return itemsPerPage;
    }

    // load next items
    public void loadNext(Object...params)
    {
        Timber.d("loadNext ");
        if (loadMode != LoadMode.IDLE)
        {
            onBusy();
            return;
        }
        loadMode = LoadMode.NEXT;
        D newestItem = items.isEmpty() ? null : items.get(0);
        onLoadNext(newestItem);
    }

    protected boolean isBusy() {
        return loadMode != LoadMode.IDLE;
    }

    protected void setNotBusy() {
        loadMode = LoadMode.IDLE;
    }

    public void loadPrevious(Object...params)
    {
        Timber.d("loadPrevious ");
        if (loadMode != LoadMode.IDLE)
        {
            onBusy();
            return;
        }

        loadMode = LoadMode.PREVIOUS;
        D oldestItem = items.isEmpty() ? null : items.get(items.size() - 1);
        onLoadPrevious(oldestItem);
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
                switch (loadMode)
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
            setNotBusy();
        }
    }

    protected abstract void onLoadNext(D endItem);
    protected abstract void onLoadPrevious(D startItem);

    public LoadMode getLoadMode()
    {
        return loadMode;
    }
}
