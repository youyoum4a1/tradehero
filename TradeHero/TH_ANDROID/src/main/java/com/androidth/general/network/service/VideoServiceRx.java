package com.androidth.general.network.service;

import com.androidth.general.api.education.PaginatedVideoCategoryDTO;
import com.androidth.general.api.education.PaginatedVideoDTO;
import com.androidth.general.api.education.VideoDTO;
import java.util.List;
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

    @GET("/fxvideos")
    Observable<List<VideoDTO>> getFXVideos();
}
