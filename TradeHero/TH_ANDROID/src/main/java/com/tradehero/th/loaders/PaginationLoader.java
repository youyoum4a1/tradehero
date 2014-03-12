package com.tradehero.th.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.tradehero.th.api.PaginatedDTO;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/10/13 Time: 5:02 PM Copyright (c) TradeHero
 */
public abstract class PaginationLoader<D> extends AsyncTaskLoader<PaginatedDTO<D>>
{
    private static final int DEFAULT_ITEM_PER_PAGE = 10;
    private int itemsPerPage = DEFAULT_ITEM_PER_PAGE;

    private LoadMode currentLoadMode = LoadMode.IDLE;

    public PaginationLoader(Context context)
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
    }

    public void loadPrevious()
    {
    }

    @Override public void deliverResult(PaginatedDTO<D> data)
    {
    }

    protected abstract void onLoadNext(D endItem);
    protected abstract void onLoadPrevious(D startItem);
}
