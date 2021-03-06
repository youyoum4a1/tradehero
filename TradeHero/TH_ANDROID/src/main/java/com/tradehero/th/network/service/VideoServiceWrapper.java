package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.education.PagedVideoCategories;
import com.tradehero.th.api.education.PagedVideoCategoryId;
import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.PaginatedVideoDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class VideoServiceWrapper
{
    @NonNull private final VideoServiceRx videoServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public VideoServiceWrapper(
            @NonNull VideoServiceRx videoServiceRx)
    {
        this.videoServiceRx = videoServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Video Categories">
    @NonNull public Observable<PaginatedVideoCategoryDTO> getVideoCategoriesRx(@Nullable PagedVideoCategories pagedVideoCategories)
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
    @NonNull public Observable<PaginatedVideoDTO> getVideosRx(@NonNull VideoCategoryId videoCategoryId)
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
