package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.education.PagedVideoCategories;
import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.network.service.VideoServiceWrapper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache @Deprecated
public class PaginatedVideoCategoryCache extends StraightCutDTOCacheNew<PagedVideoCategories, PaginatedVideoCategoryDTO, PaginatedVideoCategoryId>
{
    private static final int DEFAULT_MAX_SIZE = 20;

    @NonNull private final VideoCategoryCacheRx videoCategoryCache;
    @NonNull private final VideoServiceWrapper videoServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public PaginatedVideoCategoryCache(
            @NonNull VideoCategoryCacheRx videoCategoryCache,
            @NonNull VideoServiceWrapper videoServiceWrapper,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.videoCategoryCache = videoCategoryCache;
        this.videoServiceWrapper = videoServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override public PaginatedVideoCategoryDTO fetch(@NonNull PagedVideoCategories key) throws Throwable
    {
        return videoServiceWrapper.getVideoCategories(key);
    }

    @NonNull @Override protected PaginatedVideoCategoryId cutValue(@NonNull PagedVideoCategories key, @NonNull PaginatedVideoCategoryDTO value)
    {
        return new PaginatedVideoCategoryId(videoCategoryCache, value);
    }

    @Nullable @Override protected PaginatedVideoCategoryDTO inflateValue(@NonNull PagedVideoCategories key, @Nullable PaginatedVideoCategoryId cutValue)
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
