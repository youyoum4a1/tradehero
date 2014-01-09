package com.tradehero.th.loaders;

import android.content.Context;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/10/13 Time: 5:02 PM Copyright (c) TradeHero
 */
public abstract class PaginationListLoader<D> extends ListLoader<D>
{
    private static final int DEFAULT_ITEM_PER_PAGE = 10;
    private int itemsPerPage = DEFAULT_ITEM_PER_PAGE;

    private LoadMode currentLoadMode = LoadMode.IDLE;

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
    public void loadNext()
    {
        if (currentLoadMode != LoadMode.IDLE)
        {
            onBusy();
            return;
        }
        currentLoadMode = LoadMode.NEXT;
        D newestItem = items.isEmpty() ? null : items.get(0);
        onLoadNext(newestItem);
    }

    public void loadPrevious()
    {
        if (currentLoadMode != LoadMode.IDLE)
        {
            onBusy();
            return;
        }

        currentLoadMode = LoadMode.PREVIOUS;
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

    protected abstract void onLoadNext(D endItem);
    protected abstract void onLoadPrevious(D startItem);
}
