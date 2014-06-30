package com.tradehero.th.network.service;

import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCategoryDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.key.NewsItemListGlobalKey;
import com.tradehero.th.api.news.key.NewsItemListInterestKey;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListRegionalKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.news.key.NewsItemListSocialKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

@Singleton public class NewsServiceWrapper
{
    @NotNull private final NewsServiceSync newsServiceSync;
    @NotNull private final NewsServiceAsync newsServiceAsync;

    @Inject public NewsServiceWrapper(
            @NotNull NewsServiceSync newsServiceSync,
            @NotNull NewsServiceAsync newsServiceAsync)
    {
        this.newsServiceSync = newsServiceSync;
        this.newsServiceAsync = newsServiceAsync;
    }

    public PaginatedDTO<CountryLanguagePairDTO> getCountryLanguagePairs()
    {
        return newsServiceSync.getCountryLanguagePairs();
    }

    public MiddleCallback<PaginatedDTO<CountryLanguagePairDTO>> getCountryLanguagePairs(Callback<PaginatedDTO<CountryLanguagePairDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<CountryLanguagePairDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getCountryLanguagePairs(middleCallback);
        return middleCallback;
    }

    public PaginatedDTO<NewsItemCategoryDTO> getNewsCategories()
    {
        return newsServiceSync.getCategories();
    }

    public MiddleCallback<PaginatedDTO<NewsItemCategoryDTO>> getNewsCategories(Callback<PaginatedDTO<NewsItemCategoryDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<NewsItemCategoryDTO>>
                middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getCategories(middleCallback);
        return middleCallback;
    }

    public PaginatedDTO<NewsItemCompactDTO> getNews(NewsItemListKey key)
    {
        if (key instanceof NewsItemListGlobalKey)
        {
            return getGlobalNews((NewsItemListGlobalKey) key);
        }
        else if (key instanceof NewsItemListRegionalKey)
        {
            return getRegionalNews((NewsItemListRegionalKey) key);
        }
        else if (key instanceof NewsItemListSocialKey)
        {
            return getSocialNews((NewsItemListSocialKey) key);
        }
        else if (key instanceof NewsItemListInterestKey)
        {
            return getOfInterest((NewsItemListInterestKey) key);
        }
        else if (key instanceof NewsItemListSecurityKey)
        {
            return getSecurityNews((NewsItemListSecurityKey) key);
        }
        else
        {
            throw new IllegalStateException("Unhandled type " + key.getClass());
        }
    }

    public MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> getNews(
            NewsItemListKey key,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback)
    {
        if (key instanceof NewsItemListGlobalKey)
        {
            return getGlobalNews((NewsItemListGlobalKey) key, callback);
        }
        else if (key instanceof NewsItemListRegionalKey)
        {
            return getRegionalNews((NewsItemListRegionalKey) key, callback);
        }
        else if (key instanceof NewsItemListSocialKey)
        {
            return getSocialNews((NewsItemListSocialKey) key, callback);
        }
        else if (key instanceof NewsItemListInterestKey)
        {
            return getOfInterest((NewsItemListInterestKey) key, callback);
        }
        else if (key instanceof NewsItemListSecurityKey)
        {
            return getSecurityNews((NewsItemListSecurityKey) key, callback);
        }
        else
        {
            throw new IllegalStateException("Unhandled type " + key.getClass());
        }
    }

    public PaginatedDTO<NewsItemCompactDTO> getRegionalNews(NewsItemListRegionalKey key)
    {
        return newsServiceSync.getRegional(key.countryCode, key.languageCode, key.page, key.perPage);
    }

    public MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> getRegionalNews(
            NewsItemListRegionalKey key,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getRegional(key.countryCode, key.languageCode, key.page, key.perPage,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> getGlobalNews(
            NewsItemListGlobalKey key,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback)
    {

        MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getGlobal(key.page, key.perPage, middleCallback);
        return middleCallback;
    }

    public PaginatedDTO<NewsItemCompactDTO> getGlobalNews(NewsItemListGlobalKey key)
    {
        return newsServiceSync.getGlobal(key.page, key.perPage);
    }

    public MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> getSocialNews(
            NewsItemListSocialKey key,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback)
    {

        MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getSocial(key.categoryId, key.page, key.perPage, middleCallback);
        return middleCallback;
    }

    public PaginatedDTO<NewsItemCompactDTO> getSocialNews(NewsItemListSocialKey key)
    {
        return newsServiceSync.getSocial(key.categoryId, key.page, key.perPage);
    }

    public PaginatedDTO<NewsItemCompactDTO> getOfInterest(NewsItemListInterestKey key)
    {
        return newsServiceSync.getOfInterest(key.page, key.perPage);
    }

    public MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> getOfInterest(
            NewsItemListInterestKey key,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getOfInterest(key.page, key.perPage, middleCallback);
        return middleCallback;
    }

    public PaginatedDTO<NewsItemCompactDTO> getSecurityNews(NewsItemListSecurityKey key)
    {
        return newsServiceSync.getSecuritiesNewsList(key.securityIntegerId.key, key.page,
                key.perPage);
    }

    public MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> getSecurityNews(
            NewsItemListSecurityKey key,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<NewsItemCompactDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getSecuritiesNewsList(key.securityIntegerId.key, key.page, key.perPage,
                middleCallback);
        return middleCallback;
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
}
