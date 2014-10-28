package com.tradehero.th.network.service;

import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCategoryDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemSourceDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface NewsServiceRx
{
    @GET("/news/countries") Observable<PaginatedDTO<CountryLanguagePairDTO>> getCountryLanguagePairsRx();

    @GET("/news/categories") Observable<PaginatedDTO<NewsItemCategoryDTO>> getCategoriesRx();

    @GET("/news/sources") Observable<PaginatedDTO<NewsItemSourceDTO>> getSourcesRx();

    @GET("/news/regional") Observable<PaginatedDTO<NewsItemCompactDTO>> getRegionalRx(
            @Query("countryCode") String countryCode,
            @Query("languageCode") String languageCode,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/global") Observable<PaginatedDTO<NewsItemCompactDTO>> getGlobalRx(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/social") Observable<PaginatedDTO<NewsItemCompactDTO>> getSocialRx(
            @Query("categoryId") int categoryId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/ofinterest") Observable<PaginatedDTO<NewsItemCompactDTO>> getOfInterestRx(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/securities") Observable<PaginatedDTO<NewsItemCompactDTO>> getSecuritiesNewsListRx(
            @Query("securityId") int securityId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/featured") Observable<PaginatedDTO<NewsItemCompactDTO>> getFeaturedNewsListRx(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
}
