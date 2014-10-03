package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemListFeaturedKey;
import com.tradehero.th.api.news.key.NewsItemListGlobalKey;
import com.tradehero.th.api.news.key.NewsItemListInterestKey;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListRegionalKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.news.key.NewsItemListSocialKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCategoryDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

@Singleton public class NewsServiceWrapper
{
    @NotNull private final NewsService newsService;
    @NotNull private final NewsServiceAsync newsServiceAsync;

    //<editor-fold desc="Constructors">
    @Inject public NewsServiceWrapper(
            @NotNull NewsService newsService,
            @NotNull NewsServiceAsync newsServiceAsync)
    {
        this.newsService = newsService;
        this.newsServiceAsync = newsServiceAsync;
    }
    //</editor-fold>

    public PaginatedDTO<CountryLanguagePairDTO> getCountryLanguagePairs()
    {
        return newsService.getCountryLanguagePairs();
    }

    public MiddleCallback<PaginatedDTO<CountryLanguagePairDTO>> getCountryLanguagePairs(Callback<PaginatedDTO<CountryLanguagePairDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<CountryLanguagePairDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getCountryLanguagePairs(middleCallback);
        return middleCallback;
    }

    public PaginatedDTO<NewsItemCategoryDTO> getNewsCategories()
    {
        return newsService.getCategories();
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
        else if (key instanceof NewsItemListFeaturedKey)
        {
            return getFeaturedNews((NewsItemListFeaturedKey) key);
        }
        else
        {
            throw new IllegalStateException("Unhandled type " + key.getClass());
        }
    }

    private PaginatedDTO<NewsItemCompactDTO> getFeaturedNews(NewsItemListFeaturedKey key)
    {
        return newsService.getFeaturedNewsList(key.page, key.perPage);
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
        return newsService.getRegional(key.countryCode, key.languageCode, key.page, key.perPage);
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
        return newsService.getGlobal(key.page, key.perPage);
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
        return newsService.getSocial(key.categoryId, key.page, key.perPage);
    }

    public PaginatedDTO<NewsItemCompactDTO> getOfInterest(NewsItemListInterestKey key)
    {
        return newsService.getOfInterest(key.page, key.perPage);
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
        return newsService.getSecuritiesNewsList(key.securityIntegerId.key, key.page,
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

    public NewsItemDTO getSecurityNewsDetail(DiscussionKey discussionKey)
    {
        return newsService.getNewsDetails(discussionKey.id);
    }

    public MiddleCallback<NewsItemDTO> getSecurityNewsDetail(DiscussionKey discussionKey, Callback<NewsItemDTO> callback)
    {
        MiddleCallback<NewsItemDTO> middleCallback = new BaseMiddleCallback<>(callback);
        newsServiceAsync.getNewsDetails(discussionKey.id, middleCallback);
        return middleCallback;
    }
}
