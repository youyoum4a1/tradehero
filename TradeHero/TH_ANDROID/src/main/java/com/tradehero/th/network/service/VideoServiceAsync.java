package com.tradehero.th.network.service;

import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.PaginatedVideoDTO;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

interface VideoServiceAsync
{
    @GET("/videoCategories")
    void getVideoCategories(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<PaginatedVideoCategoryDTO> callback);

    @GET("videos/{videoCategoryId}")
    void getVideos(
            @Path("videoCategoryId") int videoCategoryId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<PaginatedVideoDTO> callback);
}
