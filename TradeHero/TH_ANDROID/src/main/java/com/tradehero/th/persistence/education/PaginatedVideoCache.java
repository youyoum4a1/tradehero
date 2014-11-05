package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.education.PaginatedVideoDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import com.tradehero.th.network.service.VideoServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @UserCache @Deprecated
public class PaginatedVideoCache extends StraightCutDTOCacheNew<VideoCategoryId, PaginatedVideoDTO, PaginatedVideoId>
{
    private static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final VideoCacheRx videoCache;
    @NotNull private final VideoServiceWrapper videoServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public PaginatedVideoCache(
            @NotNull VideoCacheRx videoCache,
            @NotNull VideoServiceWrapper videoServiceWrapper,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.videoCache = videoCache;
        this.videoServiceWrapper = videoServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override public PaginatedVideoDTO fetch(@NotNull VideoCategoryId key) throws Throwable
    {
        return videoServiceWrapper.getVideos(key);
    }

    @NotNull @Override protected PaginatedVideoId cutValue(@NotNull VideoCategoryId key, @NotNull PaginatedVideoDTO value)
    {
        return new PaginatedVideoId(videoCache, value);
    }

    @Nullable @Override protected PaginatedVideoDTO inflateValue(@NotNull VideoCategoryId key, @Nullable PaginatedVideoId cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        PaginatedVideoDTO inflated = cutValue.create(videoCache);
        if (inflated.getData() != null && inflated.hasNullItem())
        {
            return null;
        }
        return inflated;
    }

    @Nullable @Override public PaginatedVideoDTO get(@NotNull VideoCategoryId key)
    {
        return super.get(key);
    }
}
