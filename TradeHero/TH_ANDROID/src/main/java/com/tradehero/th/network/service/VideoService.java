package com.tradehero.th.network.service;

import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.PaginatedVideoDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface VideoService
{
    @GET("/videoCategories")
    PaginatedVideoCategoryDTO getVideoCategories(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/videos")
    PaginatedVideoDTO getVideos(
            @Query("videoCategoryId") int videoCategoryId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
}
