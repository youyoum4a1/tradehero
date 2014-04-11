package com.tradehero.th.loaders;

import android.content.Context;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/10/13 Time: 5:02 PM Copyright (c) TradeHero
 */
public abstract class PaginatedLoader<D> extends PaginationListLoader<D>
{
    public PaginatedLoader(Context context)
    {
        super(context);
    }

    protected abstract void onLoadNext(D endItem);
    protected abstract void onLoadPrevious(D startItem);
    protected abstract PaginatedDTO<DiscussionDTO> getPaginatedDTO();
}
