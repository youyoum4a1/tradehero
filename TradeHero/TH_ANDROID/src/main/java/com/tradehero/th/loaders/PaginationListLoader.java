package com.tradehero.th.loaders;

import android.content.Context;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/10/13 Time: 5:02 PM Copyright (c) TradeHero
 */
public abstract class PaginationListLoader<D extends ItemWithComparableId> extends ListLoader<D>
{
    private static final int DEFAULT_ITEM_PER_PAGE = 10;
    private int itemsPerPage = DEFAULT_ITEM_PER_PAGE;

    private LoadMode currentLoadMode;
    private int page;

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

        if (!items.isEmpty())
        {
            onLoadNext(items.get(items.size() - 1));
        }
    }

    public void loadPrevious()
    {
        if (currentLoadMode != LoadMode.IDLE)
        {
            onBusy();
            return;
        }
        currentLoadMode = LoadMode.PREVIOUS;

        if (items.size() > 0)
        {
            onLoadPrevious(items.get(0));
        }
    }

    protected abstract void onLoadNext(D endItem);
    protected abstract void onLoadPrevious(D startItem);
}
