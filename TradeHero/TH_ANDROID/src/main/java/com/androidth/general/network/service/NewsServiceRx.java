package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.news.CountryLanguagePairDTO;
import com.androidth.general.api.news.NewsItemCategoryDTO;
import com.androidth.general.api.news.NewsItemCompactDTO;
import com.androidth.general.api.news.NewsItemDTO;
import com.androidth.general.api.news.NewsItemSourceDTO;
import com.androidth.general.api.pagination.PaginatedDTO;
import com.androidth.general.api.timeline.TimelineItemShareRequestDTO;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface NewsServiceRx
{
    @GET("api/news/countries") Observable<PaginatedDTO<CountryLanguagePairDTO>> getCountryLanguagePairs();

    @GET("api/news/categories") Observable<PaginatedDTO<NewsItemCategoryDTO>> getCategories();

    @GET("api/news/sources") Observable<PaginatedDTO<NewsItemSourceDTO>> getSources();

    @GET("api/news/regional") Observable<PaginatedDTO<NewsItemCompactDTO>> getRegional(
            @Query("countryCode") String countryCode,
            @Query("languageCode") String languageCode,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/news/global") Observable<PaginatedDTO<NewsItemCompactDTO>> getGlobal(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/news/social") Observable<PaginatedDTO<NewsItemCompactDTO>> getSocial(
            @Query("categoryId") int categoryId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/news/ofinterest") Observable<PaginatedDTO<NewsItemCompactDTO>> getOfInterest(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/news/securities") Observable<PaginatedDTO<NewsItemCompactDTO>> getSecuritiesNewsList(
            @Query("securityId") int securityId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/news/securitiesv2") Observable<PaginatedDTO<NewsItemCompactDTO>> getSecuritiesV2List(
            @Query("securityId") int securityId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/news/featured") Observable<PaginatedDTO<NewsItemCompactDTO>> getFeaturedNewsList(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/news/{newsId}") Observable<NewsItemDTO> getNewsDetails(@Path("newsId") long newsId);

    @POST("api/discussions/news/{headlineItemId}/share") Observable<BaseResponseDTO> shareHeadlineItem(
            @Path("headlineItemId") int headlineItemId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO);

    @GET("api/news/seekingalpha") Observable<PaginatedDTO<NewsItemCompactDTO>> getSeekingAlpha(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/news/integratednews") Observable<PaginatedDTO<NewsItemCompactDTO>> getIntegratedNews(
            @Query("countryCode") String countryCode,
            @Query("languageCode") String languageCode,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
}
