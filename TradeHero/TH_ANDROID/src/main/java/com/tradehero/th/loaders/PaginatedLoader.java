package com.tradehero.th.loaders;

import android.content.Context;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;

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
