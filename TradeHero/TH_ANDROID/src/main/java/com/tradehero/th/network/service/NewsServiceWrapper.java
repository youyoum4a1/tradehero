package com.tradehero.th.network.service;

import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCategoryDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;

@Singleton public class NewsServiceWrapper
{
    private final NewsServiceSync newsServiceSync;
    private final NewsServiceAsync newsServiceAsync;

    @Inject public NewsServiceWrapper(
            NewsServiceSync newsServiceSync,
            NewsServiceAsync newsServiceAsync)
    {
        this.newsServiceSync = newsServiceSync;
        this.newsServiceAsync = newsServiceAsync;
    }

    public MiddleCallback<PaginatedDTO<NewsItemDTO>> getSecurityNews(int securityId, int page, int perPage, Callback<PaginatedDTO<NewsItemDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<NewsItemDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getSecuritiesNewsList(securityId, page, perPage, middleCallback);
        return middleCallback;
    }

    public PaginatedDTO<NewsItemDTO> getSecurityNews(int securityId, int page, int perPage)
    {
        return newsServiceSync.getSecuritiesNewsList(securityId, page, perPage);
    }

    public PaginatedDTO<NewsItemDTO> getSecurityNews(int securityId)
    {
        return getSecurityNews(securityId, 0, 42);
    }

    public NewsItemDTO getSecurityNewsDetail(long newsId)
    {
        return newsServiceSync.getNewsDetails(newsId);
    }

    public MiddleCallback<NewsItemDTO> getSecurityNewsDetail(long newsId, Callback<NewsItemDTO> callback)
    {
        MiddleCallback<NewsItemDTO> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getNewsDetails(newsId, middleCallback);
        return middleCallback;
    }

    /**
     * countries
     */
    public MiddleCallback<PaginatedDTO<CountryLanguagePairDTO>> getCountryLanguagePairs(Callback<PaginatedDTO<CountryLanguagePairDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<CountryLanguagePairDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getCountryLanguagePairs(middleCallback);
        return middleCallback;
    }

    /**
     * countries
     */
    public PaginatedDTO<CountryLanguagePairDTO> getCountryLanguagePairs()
    {
        return newsServiceSync.getCountryLanguagePairs();
    }

    /**
     * social categories
     */
    public MiddleCallback<PaginatedDTO<NewsItemCategoryDTO>> getNewsCategories(Callback<PaginatedDTO<NewsItemCategoryDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<NewsItemCategoryDTO>>
                middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getCategories(middleCallback);
        return middleCallback;
    }

    /**
     * social categories
     */
    public PaginatedDTO<NewsItemCategoryDTO> getNewsCategories()
    {
        return newsServiceSync.getCategories();
    }

    /**
     * news of specific region
     */
    public MiddleCallback<PaginatedDTO<NewsItemDTO>> getRegionalNews(
            String countryCode,
            String languageCode,
            int page/*    = 1*/,
            int perPage/* = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<NewsItemDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getRegional(countryCode, languageCode, page, perPage, middleCallback);
        return middleCallback;
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
    public MiddleCallback<PaginatedDTO<NewsItemDTO>> getGlobalNews(
            int page/*    = 1*/,
            int perPage/* = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback) throws RetrofitError
    {

        MiddleCallback<PaginatedDTO<NewsItemDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getGlobal(page, perPage, middleCallback);
        return middleCallback;
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
    public MiddleCallback<PaginatedDTO<NewsItemDTO>> getSocialNews(
            int categoryId,
            int page/*        = 1*/,
            int perPage/*     = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback)
    {

        MiddleCallback<PaginatedDTO<NewsItemDTO>> middleCallback = new BaseMiddleCallback<>(callback);
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
    )
    {
        return newsServiceSync.getSocial(categoryId, page, perPage);
    }

    /**
     * my headlines
     */
    public MiddleCallback<PaginatedDTO<NewsItemDTO>> getOfInterest(
            int page/*    = 1*/,
            int perPage/* = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<NewsItemDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getOfInterest(page, perPage, middleCallback);
        return middleCallback;
    }

    /**
     * my headlines
     */
    public PaginatedDTO<NewsItemDTO> getOfInterest(
            int page/*    = 1*/,
            int perPage/* = 42*/)
    {
        return newsServiceSync.getOfInterest(page, perPage);
    }

    // please write your own for all async method inside NewsServiceAsync
}
