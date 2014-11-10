package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCategoryDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.key.NewsItemListFeaturedKey;
import com.tradehero.th.api.news.key.NewsItemListGlobalKey;
import com.tradehero.th.api.news.key.NewsItemListInterestKey;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListRegionalKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.news.key.NewsItemListSocialKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.models.discussion.NewsDTOProcessor;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class NewsServiceWrapper
{
    @NonNull private final NewsServiceRx newsServiceRx;
    @NonNull private final Provider<NewsDTOProcessor> newsDTOProcessorProvider;

    //<editor-fold desc="Constructors">
    @Inject public NewsServiceWrapper(
            @NonNull NewsServiceRx newsServiceRx,
            @NonNull Provider<NewsDTOProcessor> newsDTOProcessorProvider)
    {
        this.newsServiceRx = newsServiceRx;
        this.newsDTOProcessorProvider = newsDTOProcessorProvider;
    }
    //</editor-fold>

    public Observable<PaginatedDTO<CountryLanguagePairDTO>> getCountryLanguagePairsRx()
    {
        return newsServiceRx.getCountryLanguagePairs();
    }

    public Observable<PaginatedDTO<NewsItemCategoryDTO>> getNewsCategoriesRx()
    {
        return newsServiceRx.getCategories();
    }

    public Observable<PaginatedDTO<NewsItemCompactDTO>> getNewsRx(NewsItemListKey key)
    {
        Observable<PaginatedDTO<NewsItemCompactDTO>> paginatedNewsItemCompactDTO;
        if (key instanceof NewsItemListGlobalKey)
        {
            paginatedNewsItemCompactDTO = getGlobalNewsRx((NewsItemListGlobalKey) key);
        }
        else if (key instanceof NewsItemListRegionalKey)
        {
            paginatedNewsItemCompactDTO = getRegionalNewsRx((NewsItemListRegionalKey) key);
        }
        else if (key instanceof NewsItemListSocialKey)
        {
            paginatedNewsItemCompactDTO = getSocialNewsRx((NewsItemListSocialKey) key);
        }
        else if (key instanceof NewsItemListInterestKey)
        {
            paginatedNewsItemCompactDTO = getOfInterestRx((NewsItemListInterestKey) key);
        }
        else if (key instanceof NewsItemListSecurityKey)
        {
            paginatedNewsItemCompactDTO = getSecurityNewsRx((NewsItemListSecurityKey) key);
        }
        else if (key instanceof NewsItemListFeaturedKey)
        {
            paginatedNewsItemCompactDTO = getFeaturedNewsRx((NewsItemListFeaturedKey) key);
        }
        else
        {
            throw new IllegalStateException("Unhandled type " + key.getClass());
        }

        return paginatedNewsItemCompactDTO
                .doOnNext(newsDTOProcessorProvider.get());
    }

    private Observable<PaginatedDTO<NewsItemCompactDTO>> getFeaturedNewsRx(NewsItemListFeaturedKey key)
    {
        return newsServiceRx.getFeaturedNewsList(key.page, key.perPage);
    }

    private Observable<PaginatedDTO<NewsItemCompactDTO>> getSecurityNewsRx(NewsItemListSecurityKey key)
    {
        return newsServiceRx.getSecuritiesNewsList(key.securityIntegerId.key, key.page, key.perPage);
    }

    private Observable<PaginatedDTO<NewsItemCompactDTO>> getOfInterestRx(NewsItemListInterestKey key)
    {
        return newsServiceRx.getOfInterest(key.page, key.perPage);
    }

    private Observable<PaginatedDTO<NewsItemCompactDTO>> getSocialNewsRx(NewsItemListSocialKey key)
    {
        return newsServiceRx.getSocial(key.categoryId, key.page, key.perPage);
    }

    private Observable<PaginatedDTO<NewsItemCompactDTO>> getRegionalNewsRx(NewsItemListRegionalKey key)
    {
        return newsServiceRx.getRegional(key.countryCode, key.languageCode, key.page, key.perPage);
    }

    private Observable<PaginatedDTO<NewsItemCompactDTO>> getGlobalNewsRx(NewsItemListGlobalKey key)
    {
        return newsServiceRx.getGlobal(key.page, key.perPage);
    }

    public Observable<NewsItemDTO> getSecurityNewsDetailRx(DiscussionKey discussionKey)
    {
        return newsServiceRx.getNewsDetails(discussionKey.id);
    }
}
