package com.androidth.general.network.service;

import com.androidth.general.api.education.PaginatedVideoCategoryDTO;
import com.androidth.general.api.education.PaginatedVideoDTO;
import com.androidth.general.api.education.VideoDTO;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

interface VideoServiceRx
{
    @GET("api/videoCategories")
    Observable<PaginatedVideoCategoryDTO> getVideoCategories(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/videos")
    Observable<PaginatedVideoDTO> getVideos(
            @Query("videoCategoryId") int videoCategoryId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/fxvideos")
    Observable<List<VideoDTO>> getFXVideos();
}
