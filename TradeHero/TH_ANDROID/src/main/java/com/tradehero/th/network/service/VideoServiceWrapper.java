package com.tradehero.th.network.service;

import com.tradehero.th.api.education.PagedVideoCategories;
import com.tradehero.th.api.education.PagedVideoCategoryId;
import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.PaginatedVideoDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class VideoServiceWrapper
{
    @NotNull private final VideoService videoService;
    @NotNull private final VideoServiceAsync videoServiceAsync;

    //<editor-fold desc="Constructors">
    @Inject public VideoServiceWrapper(
            @NotNull VideoService videoService,
            @NotNull VideoServiceAsync videoServiceAsync)
    {
        this.videoService = videoService;
        this.videoServiceAsync = videoServiceAsync;
    }
    //</editor-fold>

    //<editor-fold desc="Get Video Categories">
    @NotNull public PaginatedVideoCategoryDTO getVideoCategories(@Nullable PagedVideoCategories pagedVideoCategories)
    {
        if (pagedVideoCategories == null)
        {
            return videoService.getVideoCategories(null, null);
        }
        return videoService.getVideoCategories(
                pagedVideoCategories.page,
                pagedVideoCategories.perPage);
    }

    @NotNull public MiddleCallback<PaginatedVideoCategoryDTO> getVideoCategories(
            @Nullable PagedVideoCategories pagedVideoCategories,
            @Nullable Callback<PaginatedVideoCategoryDTO> callback)
    {
        MiddleCallback<PaginatedVideoCategoryDTO> middleCallback = new BaseMiddleCallback<>(callback);
        if (pagedVideoCategories == null)
        {
            videoServiceAsync.getVideoCategories(null, null, middleCallback);
        }
        else
        {
            videoServiceAsync.getVideoCategories(
                    pagedVideoCategories.page,
                    pagedVideoCategories.perPage,
                    middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Videos">
    @NotNull public PaginatedVideoDTO getVideos(@NotNull VideoCategoryId videoCategoryId)
    {
        if (videoCategoryId instanceof PagedVideoCategoryId)
        {
            return videoService.getVideos(
                    videoCategoryId.id,
                    ((PagedVideoCategoryId) videoCategoryId).page,
                    ((PagedVideoCategoryId) videoCategoryId).perPage);
        }
        return videoService.getVideos(videoCategoryId.id, null, null);
    }

    @NotNull public MiddleCallback<PaginatedVideoDTO> getVideos(
            @NotNull VideoCategoryId videoCategoryId,
            @Nullable Callback<PaginatedVideoDTO> callback)
    {
        MiddleCallback<PaginatedVideoDTO> middleCallback = new BaseMiddleCallback<>(callback);
        if (videoCategoryId instanceof PagedVideoCategoryId)
        {
            videoServiceAsync.getVideos(
                    videoCategoryId.id,
                    ((PagedVideoCategoryId) videoCategoryId).page,
                    ((PagedVideoCategoryId) videoCategoryId).perPage,
                    middleCallback);
        }
        else
        {
            videoServiceAsync.getVideos(
                    videoCategoryId.id,
                    null,
                    null,
                    middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>
}
