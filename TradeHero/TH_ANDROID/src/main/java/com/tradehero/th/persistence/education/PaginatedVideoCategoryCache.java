package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.education.PagedVideoCategories;
import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.network.service.VideoServiceWrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache @Deprecated
public class PaginatedVideoCategoryCache extends StraightCutDTOCacheNew<PagedVideoCategories, PaginatedVideoCategoryDTO, PaginatedVideoCategoryId>
{
    private static final int DEFAULT_MAX_SIZE = 20;

    @NotNull private final VideoCategoryCacheRx videoCategoryCache;
    @NotNull private final VideoServiceWrapper videoServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public PaginatedVideoCategoryCache(
            @NotNull VideoCategoryCacheRx videoCategoryCache,
            @NotNull VideoServiceWrapper videoServiceWrapper,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
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
