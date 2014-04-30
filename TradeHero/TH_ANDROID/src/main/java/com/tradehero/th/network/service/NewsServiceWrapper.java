package com.tradehero.th.network.service;

import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCategoryDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.models.news.MiddleCallbackPaginationNewsItem;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 3:55 PM Copyright (c) TradeHero
 */
@Singleton
public class NewsServiceWrapper
{
    private final NewsServiceSync newsServiceSync;
    private final NewsServiceAsync newsServiceAsync;

    @Inject public NewsServiceWrapper(NewsServiceAsync newsServiceAsync, NewsServiceSync newsServiceSync)
    {
        this.newsServiceAsync = newsServiceAsync;
        this.newsServiceSync = newsServiceSync;
    }

    public MiddleCallbackPaginationNewsItem getSecurityNews(int securityId, int page, int perPage, Callback<PaginatedDTO<NewsItemDTO>> callback)
    {
        MiddleCallbackPaginationNewsItem middleCallbackPaginationNewsItem = new MiddleCallbackPaginationNewsItem(callback);
        newsServiceAsync.getSecuritiesNewsList(securityId, page, perPage, middleCallbackPaginationNewsItem);
        return middleCallbackPaginationNewsItem;
    }

    public PaginatedDTO<NewsItemDTO> getSecurityNews(int securityId, int page, int perPage)
    {

        PaginatedDTO<NewsItemDTO> newsList = newsServiceSync.getSecuritiesNewsList(securityId, page, perPage);
        return newsList;
    }

    public PaginatedDTO<NewsItemDTO> getSecurityNews(int securityId)
    {
        return getSecurityNews(securityId, 0, 42);
    }

    public NewsItemDTO getSecurityNewsDetail(long newsId)
    {
        return newsServiceSync.getNewsDetails(newsId);
    }

    public BaseMiddleCallback<NewsItemDTO> getSecurityNewsDetail(long newsId, Callback<NewsItemDTO> callback) throws RetrofitError
    {
        BaseMiddleCallback<NewsItemDTO> middleCallback = new BaseMiddleCallback<NewsItemDTO>(callback);
        newsServiceAsync.getNewsDetails(newsId, middleCallback);
        return middleCallback;
    }

    /**
     * countries
     *
     * @throws RetrofitError
     */
    public BaseMiddleCallback getCountryLanguagePairs(Callback<PaginatedDTO<CountryLanguagePairDTO>> callback) throws RetrofitError
    {
        BaseMiddleCallback<PaginatedDTO<CountryLanguagePairDTO>>
                middleCallback = new BaseMiddleCallback<PaginatedDTO<CountryLanguagePairDTO>>(callback);
        newsServiceAsync.getCountryLanguagePairs(middleCallback);
        return middleCallback;
    }

    /**
     * countries
     *
     * @throws RetrofitError
     */
    public PaginatedDTO<CountryLanguagePairDTO> getCountryLanguagePairs() throws RetrofitError
    {
        return newsServiceSync.getCountryLanguagePairs();
    }

    /**
     * social catefories
     *
     * @throws RetrofitError
     */
    public BaseMiddleCallback getNewsCategories(Callback<PaginatedDTO<NewsItemCategoryDTO>> callback) throws RetrofitError
    {
        BaseMiddleCallback<PaginatedDTO<NewsItemCategoryDTO>>
                middleCallback = new BaseMiddleCallback<PaginatedDTO<NewsItemCategoryDTO>>(callback);
        newsServiceAsync.getCategories(middleCallback);
        return middleCallback;
    }

    /**
     * social catefories
     */
    public PaginatedDTO<NewsItemCategoryDTO> getNewsCategories() throws RetrofitError
    {
        return newsServiceSync.getCategories();
    }

    /**
     * news of specific region
     */
    public MiddleCallbackPaginationNewsItem getRegionalNews(
            String countryCode,
            String languageCode,
            int page/*    = 1*/,
            int perPage/* = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback) throws RetrofitError
    {
        MiddleCallbackPaginationNewsItem middleCallbackPaginationNewsItem = new MiddleCallbackPaginationNewsItem(callback);
        newsServiceAsync.getRegional(countryCode, languageCode, page, perPage, middleCallbackPaginationNewsItem);
        return middleCallbackPaginationNewsItem;
    }

    /**
     * news of specific region
     */
    public PaginatedDTO<NewsItemDTO> getRegionalNews(
            String countryCode,
            String languageCode,
            int page/*    = 1*/,
            int perPage/* = 42*/
    ) throws RetrofitError
    {
        return newsServiceSync.getRegional(countryCode, languageCode, page, perPage);
    }

    //    public MiddleCallbackPaginationNewsItem getGlobal(int page, int perPage, Callback<PaginatedDTO<NewsItemDTO>> callback)
    //    {
    //
    //    }

    /**
     * global news
     */
    public MiddleCallbackPaginationNewsItem getGlobalNews(
            int page/*    = 1*/,
            int perPage/* = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback) throws RetrofitError
    {

        MiddleCallbackPaginationNewsItem middleCallbackPaginationNewsItem = new MiddleCallbackPaginationNewsItem(callback);
        newsServiceAsync.getGlobal(page, perPage, middleCallbackPaginationNewsItem);
        return middleCallbackPaginationNewsItem;
    }

    /**
     * global news
     */
    public PaginatedDTO<NewsItemDTO> getGlobalNews(
            int page/*    = 1*/,
            int perPage/* = 42*/
    ) throws RetrofitError
    {
        return newsServiceSync.getGlobal(page, perPage);
    }

    /**
     * news from social media
     */
    public MiddleCallbackPaginationNewsItem getSocialNews(
            int categoryId,
            int page/*        = 1*/,
            int perPage/*     = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback) throws RetrofitError
    {

        MiddleCallbackPaginationNewsItem middleCallback = new MiddleCallbackPaginationNewsItem(callback);
        newsServiceAsync.getSocial(categoryId, page, perPage, middleCallback);
        return middleCallback;
    }

    /**
     *
     * @param categoryId
     * @param page
     * @param perPage
     * @return
     */
    public PaginatedDTO<NewsItemDTO> getSocialNews(
            int categoryId,
            int page/*        = 1*/,
            int perPage/*     = 42*/
    ) throws RetrofitError
    {
        return newsServiceSync.getSocial(categoryId, page, perPage);
    }

    /**
     * my headlines
     */
    public MiddleCallbackPaginationNewsItem getOfInterest(
            int page/*    = 1*/,
            int perPage/* = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback) throws RetrofitError
    {
        MiddleCallbackPaginationNewsItem middleCallback = new MiddleCallbackPaginationNewsItem(callback);
        newsServiceAsync.getOfInterest(page, perPage, middleCallback);
        return middleCallback;
    }

    /**
     * my headlines
     */
    public PaginatedDTO<NewsItemDTO> getOfInterest(
            int page/*    = 1*/,
            int perPage/* = 42*/) throws RetrofitError
    {
        return newsServiceSync.getOfInterest(page, perPage);
    }

    // please write your own for all async method inside NewsServiceAsync
}
