package com.tradehero.th.network.service;

import com.tradehero.th.api.PaginationDTO;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCategoryDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.NewsItemSourceDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 3:54 PM Copyright (c) TradeHero
 */
public interface NewsServiceSync
{
    @GET("/news/countries")
    PaginationDTO<CountryLanguagePairDTO> getCountryLanguagePairs();

    @GET("/news/categories")
    PaginationDTO<NewsItemCategoryDTO> getCategories();

    @GET("/news/sources")
    PaginationDTO<NewsItemSourceDTO> getSources();

    @GET("/news/regional")
    PaginationDTO<NewsItemDTO> getRegional(
            @Query("countryCode") String countryCode,
            @Query("languageCode") String languageCode,
            @Query("page") int page/*    = 1*/,
            @Query("perPage") int perPage/* = 42*/);

    @GET("/news/global")
    PaginationDTO<NewsItemDTO> getGlobal(
            @Query("page") int page/*    = 1*/,
            @Query("perPage") int perPage/* = 42*/);

    @GET("/news/social")
    PaginationDTO<NewsItemDTO> getSocial(
            @Query("categoryId") int categoryId,
            @Query("page") int page/*        = 1*/,
            @Query("perPage") int perPage/*     = 42*/);

    @GET("/news/ofinterest")
    PaginationDTO<NewsItemDTO> getOfInterest(
            @Query("page") int page/*    = 1*/,
            @Query("perPage") int perPage/* = 42*/);

    @GET("/news/securities")
    PaginationDTO<NewsItemDTO> getSecuritiesNewsList(
            @Query("securityId") int securityId,
            @Query("page") int page/*    = 1*/,
            @Query("perPage") int perPage/* = 42*/);

    @GET("/news/{newsId}")
    NewsItemDTO getNewsDetails(@Path("newsId") long newsId);
}
