package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.news.CountryLanguagePairDTO;
import com.androidth.general.api.news.NewsItemCategoryDTO;
import com.androidth.general.api.news.NewsItemCompactDTO;
import com.androidth.general.api.news.NewsItemDTO;
import com.androidth.general.api.news.NewsItemSourceDTO;
import com.androidth.general.api.pagination.PaginatedDTO;
import com.androidth.general.api.timeline.TimelineItemShareRequestDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface NewsServiceRx
{
    @GET("/news/countries") Observable<PaginatedDTO<CountryLanguagePairDTO>> getCountryLanguagePairs();

    @GET("/news/categories") Observable<PaginatedDTO<NewsItemCategoryDTO>> getCategories();

    @GET("/news/sources") Observable<PaginatedDTO<NewsItemSourceDTO>> getSources();

    @GET("/news/regional") Observable<PaginatedDTO<NewsItemCompactDTO>> getRegional(
            @Query("countryCode") String countryCode,
            @Query("languageCode") String languageCode,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/global") Observable<PaginatedDTO<NewsItemCompactDTO>> getGlobal(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/social") Observable<PaginatedDTO<NewsItemCompactDTO>> getSocial(
            @Query("categoryId") int categoryId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/ofinterest") Observable<PaginatedDTO<NewsItemCompactDTO>> getOfInterest(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/securities") Observable<PaginatedDTO<NewsItemCompactDTO>> getSecuritiesNewsList(
            @Query("securityId") int securityId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/securitiesv2") Observable<PaginatedDTO<NewsItemCompactDTO>> getSecuritiesV2List(
            @Query("securityId") int securityId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/featured") Observable<PaginatedDTO<NewsItemCompactDTO>> getFeaturedNewsList(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/{newsId}") Observable<NewsItemDTO> getNewsDetails(@Path("newsId") long newsId);

    @POST("/discussions/news/{headlineItemId}/share") Observable<BaseResponseDTO> shareHeadlineItem(
            @Path("headlineItemId") int headlineItemId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO);

    @GET("/news/seekingalpha") Observable<PaginatedDTO<NewsItemCompactDTO>> getSeekingAlpha(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/integratednews") Observable<PaginatedDTO<NewsItemCompactDTO>> getIntegratedNews(
            @Query("countryCode") String countryCode,
            @Query("languageCode") String languageCode,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
}
