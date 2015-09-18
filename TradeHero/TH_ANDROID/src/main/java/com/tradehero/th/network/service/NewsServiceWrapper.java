package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedPagedDTOKey;
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
import com.tradehero.th.api.news.key.NewsItemListSeekingAlphaKey;
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

    @NonNull public Observable<PaginatedDTO<CountryLanguagePairDTO>> getCountryLanguagePairsRx()
    {
        return newsServiceRx.getCountryLanguagePairs();
    }

    @NonNull public Observable<PaginatedDTO<NewsItemCategoryDTO>> getNewsCategoriesRx()
    {
        return newsServiceRx.getCategories();
    }

    @NonNull public Observable<PaginatedDTO<NewsItemCompactDTO>> getNewsRx(NewsItemListKey key)
    {
        Observable<PaginatedDTO<NewsItemCompactDTO>> paginatedNewsItemCompactDTO;
        if (key instanceof NewsItemListGlobalKey)
        {
            paginatedNewsItemCompactDTO = newsServiceRx.getGlobal(key.page, key.perPage);
        }
        else if (key instanceof NewsItemListRegionalKey)
        {
            paginatedNewsItemCompactDTO = newsServiceRx.getRegional(
                    ((NewsItemListRegionalKey) key).countryCode,
                    ((NewsItemListRegionalKey) key).languageCode,
                    key.page,
                    key.perPage);
        }
        else if (key instanceof NewsItemListSocialKey)
        {
            paginatedNewsItemCompactDTO = newsServiceRx.getSocial(
                    ((NewsItemListSocialKey) key).categoryId,
                    key.page,
                    key.perPage);
        }
        else if (key instanceof NewsItemListInterestKey)
        {
            paginatedNewsItemCompactDTO = newsServiceRx.getOfInterest(
                    key.page,
                    key.perPage);
        }
        else if (key instanceof NewsItemListSecurityKey)
        {
            paginatedNewsItemCompactDTO = newsServiceRx.getSecuritiesNewsList(
                    ((NewsItemListSecurityKey) key).securityIntegerId.key,
                    key.page,
                    key.perPage);
        }
        else if (key instanceof NewsItemListFeaturedKey)
        {
            paginatedNewsItemCompactDTO = newsServiceRx.getFeaturedNewsList(
                    key.page,
                    key.perPage);
        }
        else if (key instanceof NewsItemListSeekingAlphaKey)
        {
            paginatedNewsItemCompactDTO = newsServiceRx.getSeekingAlpha(key.page, key.perPage);
        }
        else
        {
            return Observable.error(new IllegalStateException("Unhandled type " + key.getClass()));
        }

        return paginatedNewsItemCompactDTO
                .map(newsDTOProcessorProvider.get());
    }

    @NonNull public Observable<PaginatedDTO<NewsItemCompactDTO>> getIntegratedNews(NewsfeedPagedDTOKey newsfeedPagedDTOKey)
    {
        return newsServiceRx.getIntegratedNews(newsfeedPagedDTOKey.countryCode, newsfeedPagedDTOKey.languageCode, newsfeedPagedDTOKey.page, newsfeedPagedDTOKey.perPage);
    }

    @NonNull public Observable<NewsItemDTO> getSecurityNewsDetailRx(DiscussionKey discussionKey)
    {
        return newsServiceRx.getNewsDetails(discussionKey.id);
    }
}
