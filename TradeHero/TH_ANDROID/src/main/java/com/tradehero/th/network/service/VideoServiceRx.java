package com.tradehero.th.network.service;

import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.PaginatedVideoDTO;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

interface VideoServiceRx
{
    @GET("/videoCategories")
    Observable<PaginatedVideoCategoryDTO> getVideoCategories(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/videos")
    Observable<PaginatedVideoDTO> getVideos(
            @Query("videoCategoryId") int videoCategoryId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
}
