package com.androidth.general.persistence.education;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.education.PagedVideoCategories;
import com.androidth.general.api.education.PaginatedVideoCategoryDTO;
import com.androidth.general.api.education.VideoCategoryDTO;
import com.androidth.general.network.service.VideoServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class PaginatedVideoCategoryCacheRx extends BaseFetchDTOCacheRx<PagedVideoCategories, PaginatedVideoCategoryDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 20;

    @NonNull private final VideoCategoryCacheRx videoCategoryCache;
    @NonNull private final VideoServiceWrapper videoServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public PaginatedVideoCategoryCacheRx(
            @NonNull VideoCategoryCacheRx videoCategoryCache,
            @NonNull VideoServiceWrapper videoServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.videoCategoryCache = videoCategoryCache;
        this.videoServiceWrapper = videoServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<PaginatedVideoCategoryDTO> fetch(@NonNull PagedVideoCategories key)
    {
        return videoServiceWrapper.getVideoCategoriesRx(key);
    }

    @Override public void onNext(@NonNull PagedVideoCategories key, @NonNull PaginatedVideoCategoryDTO value)
    {
        List<VideoCategoryDTO> data = value.getData();
        if (data != null)
        {
            videoCategoryCache.onNext(data);
        }
        super.onNext(key, value);
    }
}
