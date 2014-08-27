package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.education.PagedVideoCategories;
import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.network.service.VideoServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class PaginatedVideoCategoryCache extends StraightCutDTOCacheNew<PagedVideoCategories, PaginatedVideoCategoryDTO, PaginatedVideoCategoryId>
{
    private static final int DEFAULT_MAX_SIZE = 20;

    @NotNull private final VideoCategoryCache videoCategoryCache;
    @NotNull private final VideoServiceWrapper videoServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public PaginatedVideoCategoryCache(
            @NotNull VideoCategoryCache videoCategoryCache,
            @NotNull VideoServiceWrapper videoServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.videoCategoryCache = videoCategoryCache;
        this.videoServiceWrapper = videoServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override public PaginatedVideoCategoryDTO fetch(@NotNull PagedVideoCategories key) throws Throwable
    {
        return videoServiceWrapper.getVideoCategories(key);
    }

    @NotNull @Override protected PaginatedVideoCategoryId cutValue(@NotNull PagedVideoCategories key, @NotNull PaginatedVideoCategoryDTO value)
    {
        return new PaginatedVideoCategoryId(videoCategoryCache, value);
    }

    @Nullable @Override protected PaginatedVideoCategoryDTO inflateValue(@NotNull PagedVideoCategories key, @Nullable PaginatedVideoCategoryId cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        PaginatedVideoCategoryDTO inflated = cutValue.create(videoCategoryCache);
        if (inflated.getData() != null && inflated.hasNullItem())
        {
            return null;
        }
        return inflated;
    }
}
