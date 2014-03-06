package com.tradehero.th.network.service;

import com.tradehero.th.api.PaginationDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.models.news.MiddleCallbackPaginationNewsItem;
import javax.inject.Inject;
import retrofit.Callback;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 3:55 PM Copyright (c) TradeHero
 */
public class NewsServiceWrapper
{
    private final NewsServiceSync newsServiceSync;
    private final NewsServiceAsync newsServiceAsync;

    @Inject public NewsServiceWrapper(NewsServiceAsync newsServiceAsync, NewsServiceSync newsServiceSync)
    {
        this.newsServiceAsync = newsServiceAsync;
        this.newsServiceSync = newsServiceSync;
    }

    public MiddleCallbackPaginationNewsItem getGlobal(int page, int perPage, Callback<PaginationDTO<NewsItemDTO>> callback)
    {
        MiddleCallbackPaginationNewsItem middleCallbackPaginationNewsItem = new MiddleCallbackPaginationNewsItem(callback);
        newsServiceAsync.getGlobal(page, perPage, middleCallbackPaginationNewsItem);
        return middleCallbackPaginationNewsItem;
    }

    // please write your own for all async method inside NewsServiceAsync
}
