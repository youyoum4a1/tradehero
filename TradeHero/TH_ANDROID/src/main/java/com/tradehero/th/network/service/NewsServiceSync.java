package com.tradehero.th.network.service;

import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface NewsServiceSync
{

    @GET("/news/regional") PaginatedDTO<NewsItemCompactDTO> getRegional(
            @Query("countryCode") String countryCode,
            @Query("languageCode") String languageCode,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/global") PaginatedDTO<NewsItemCompactDTO> getGlobal(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/social") PaginatedDTO<NewsItemCompactDTO> getSocial(
            @Query("categoryId") int categoryId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/ofinterest") PaginatedDTO<NewsItemCompactDTO> getOfInterest(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/securities") PaginatedDTO<NewsItemCompactDTO> getSecuritiesNewsList(
            @Query("securityId") int securityId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/news/{newsId}")
    NewsItemDTO getNewsDetails(@Path("newsId") long newsId);

}
