package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.education.PaginatedVideoDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import com.tradehero.th.api.education.VideoDTO;
import com.tradehero.th.network.service.VideoServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class PaginatedVideoCacheRx extends BaseFetchDTOCacheRx<VideoCategoryId, PaginatedVideoDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 50;
    private static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NotNull private final VideoCacheRx videoCache;
    @NotNull private final VideoServiceWrapper videoServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public PaginatedVideoCacheRx(
            @NotNull VideoCacheRx videoCache,
            @NotNull VideoServiceWrapper videoServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.videoCache = videoCache;
        this.videoServiceWrapper = videoServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override protected Observable<PaginatedVideoDTO> fetch(@NotNull VideoCategoryId key)
    {
        return videoServiceWrapper.getVideosRx(key);
    }

    @Override public void onNext(@NotNull VideoCategoryId key, @NotNull PaginatedVideoDTO value)
    {
        List<VideoDTO> data = value.getData();
        if (data != null)
        {
            videoCache.onNext(data);
        }
        super.onNext(key, value);
    }
}
