package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.education.PagedVideoCategories;
import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryDTO;
import com.tradehero.th.network.service.VideoServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class PaginatedVideoCategoryCacheRx extends BaseFetchDTOCacheRx<PagedVideoCategories, PaginatedVideoCategoryDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 20;
    private static final int DEFAULT_MAX_SUBJECT_SIZE = 2;

    @NotNull private final VideoCategoryCacheRx videoCategoryCache;
    @NotNull private final VideoServiceWrapper videoServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public PaginatedVideoCategoryCacheRx(
            @NotNull VideoCategoryCacheRx videoCategoryCache,
            @NotNull VideoServiceWrapper videoServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.videoCategoryCache = videoCategoryCache;
        this.videoServiceWrapper = videoServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override protected Observable<PaginatedVideoCategoryDTO> fetch(@NotNull PagedVideoCategories key)
    {
        return videoServiceWrapper.getVideoCategoriesRx(key);
    }

    @Override public void onNext(@NotNull PagedVideoCategories key, @NotNull PaginatedVideoCategoryDTO value)
    {
        List<VideoCategoryDTO> data = value.getData();
        if (data != null)
        {
            videoCategoryCache.onNext(data);
        }
        super.onNext(key, value);
    }
}
