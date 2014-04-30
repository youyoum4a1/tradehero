package com.tradehero.th.models.news;

import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import retrofit.Callback;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 4:25 PM Copyright (c) TradeHero
 */
public class MiddleCallbackPaginationNewsItem extends BaseMiddleCallback<PaginatedDTO<NewsItemDTO>>
{
    public MiddleCallbackPaginationNewsItem(Callback<PaginatedDTO<NewsItemDTO>> primaryCallback)
    {
        super(primaryCallback);
    }
}
