package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.education.PaginatedVideoDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import com.tradehero.th.network.service.VideoServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Singleton @UserCache @Deprecated
public class PaginatedVideoCache extends StraightCutDTOCacheNew<VideoCategoryId, PaginatedVideoDTO, PaginatedVideoId>
{
    private static final int DEFAULT_MAX_SIZE = 50;

    @NonNull private final VideoCacheRx videoCache;
    @NonNull private final VideoServiceWrapper videoServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public PaginatedVideoCache(
            @NonNull VideoCacheRx videoCache,
            @NonNull VideoServiceWrapper videoServiceWrapper,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.videoCache = videoCache;
        this.videoServiceWrapper = videoServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override public PaginatedVideoDTO fetch(@NonNull VideoCategoryId key) throws Throwable
    {
        return videoServiceWrapper.getVideos(key);
    }

    @NonNull @Override protected PaginatedVideoId cutValue(@NonNull VideoCategoryId key, @NonNull PaginatedVideoDTO value)
    {
        return new PaginatedVideoId(videoCache, value);
    }

    @Nullable @Override protected PaginatedVideoDTO inflateValue(@NonNull VideoCategoryId key, @Nullable PaginatedVideoId cutValue)
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

    @Nullable @Override public PaginatedVideoDTO get(@NonNull VideoCategoryId key)
    {
        return super.get(key);
    }
}
