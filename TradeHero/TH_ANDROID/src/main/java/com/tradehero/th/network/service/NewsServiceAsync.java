package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.NewsDTOSet;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

interface NewsServiceAsync
{

    //news of specific region
    @GET("/news/regional") void getRegional(
            @Query("countryCode") String countryCode,
            @Query("languageCode") String languageCode,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback);

    //global news
    @GET("/news/global") void getGlobal(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback);

    //news from social media
    @GET("/news/social") void getSocial(
            @Query("categoryId") int categoryId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback);

    //my headlines
    @GET("/news/ofinterest") void getOfInterest(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback);

    @GET("/news/securities") void getSecuritiesNewsList(
            @Query("securityId") int securityId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<PaginatedDTO<NewsItemCompactDTO>> callback);

    @GET("/news/{newsId}")
    void getNewsDetails(@Path("newsId") long newsId, Callback<NewsItemDTO> callback);

    @GET("/news/topics")
    void retrieveNews(
            @Query("page") int pageNumber,
            @Query("perPage")int numberPerPage,
            Callback<NewsDTOSet> callback);
}
