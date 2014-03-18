package com.tradehero.th.network.service;

import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCategoryDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.NewsItemSourceDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 3:55 PM Copyright (c) TradeHero
 */
public interface NewsServiceAsync
{
    @GET("/news/countries") void getCountryLanguagePairs(Callback<PaginatedDTO<CountryLanguagePairDTO>> callback);

    @GET("/news/categories") void getCategories(Callback<PaginatedDTO<NewsItemCategoryDTO>> callback);

    @GET("/news/sources") void getSources(Callback<PaginatedDTO<NewsItemSourceDTO>> callback);

    @GET("/news/regional") void getRegional(
            @Query("countryCode") String countryCode,
            @Query("languageCode") String languageCode,
            @Query("page") int page/*    = 1*/,
            @Query("perPage") int perPage/* = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback);

    @GET("/news/global") void getGlobal(
            @Query("page") int page/*    = 1*/,
            @Query("perPage") int perPage/* = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback);

    @GET("/news/social") void getSocial(
            @Query("categoryId") int categoryId,
            @Query("page") int page/*        = 1*/,
            @Query("perPage") int perPage/*     = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback);

    @GET("/news/ofinterest") void getOfInterest(
            @Query("page") int page/*    = 1*/,
            @Query("perPage") int perPage/* = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback);

    @GET("/news/securities") void getSecuritiesNewsList(
            @Query("securityId") int securityId,
            @Query("page") int page/*    = 1*/,
            @Query("perPage") int perPage/* = 42*/,
            Callback<PaginatedDTO<NewsItemDTO>> callback);

    @GET("/news/{newsId}")
    void getNewsDetails(@Path("newsId") long newsId, Callback<NewsItemDTO> callback);


    @POST("/discussions/news/{headlineItemId}/share")
    void shareHeadlineItem(
            @Path("headlineItemId") int headlineItemId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            Callback<Response> callback);
}
