package com.tradehero.th.models.news;

import com.tradehero.th.api.PaginationDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 4:25 PM Copyright (c) TradeHero
 */
public class MiddleCallbackPaginationNewsItem extends MiddleCallback<PaginationDTO<NewsItemDTO>>
{
    public MiddleCallbackPaginationNewsItem(Callback<PaginationDTO<NewsItemDTO>> primaryCallback)
    {
        super(primaryCallback);
    }
}
