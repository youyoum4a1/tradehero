package com.tradehero.th.network.service;

import com.tradehero.th.api.education.PagedVideoCategories;
import com.tradehero.th.api.education.PagedVideoCategoryId;
import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.PaginatedVideoDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;

@Singleton public class VideoServiceWrapper
{
    @NotNull private final VideoService videoService;
    @NotNull private final VideoServiceRx videoServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public VideoServiceWrapper(
            @NotNull VideoService videoService,
            @NotNull VideoServiceRx videoServiceRx)
    {
        this.videoService = videoService;
        this.videoServiceRx = videoServiceRx;
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

    @NotNull public Observable<PaginatedVideoCategoryDTO> getVideoCategoriesRx(@Nullable PagedVideoCategories pagedVideoCategories)
    {
        if (pagedVideoCategories == null)
        {
            return videoServiceRx.getVideoCategories(null, null);
        }
        return videoServiceRx.getVideoCategories(
                pagedVideoCategories.page,
                pagedVideoCategories.perPage);
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

    @NotNull public Observable<PaginatedVideoDTO> getVideosRx(@NotNull VideoCategoryId videoCategoryId)
    {
        if (videoCategoryId instanceof PagedVideoCategoryId)
        {
            return videoServiceRx.getVideos(
                    videoCategoryId.id,
                    ((PagedVideoCategoryId) videoCategoryId).page,
                    ((PagedVideoCategoryId) videoCategoryId).perPage);
        }
        return videoServiceRx.getVideos(videoCategoryId.id, null, null);
    }
    //</editor-fold>
}
