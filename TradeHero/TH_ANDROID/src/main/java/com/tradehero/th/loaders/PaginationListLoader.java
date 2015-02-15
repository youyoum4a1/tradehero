package com.tradehero.th.loaders;

import android.content.Context;

import java.util.List;

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



    protected void setNotBusy() {
        loadMode = LoadMode.IDLE;
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

}
