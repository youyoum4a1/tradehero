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
import android.support.annotation.NonNull;
import rx.Observable;

@Singleton @UserCache
public class PaginatedVideoCacheRx extends BaseFetchDTOCacheRx<VideoCategoryId, PaginatedVideoDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 50;
    private static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NonNull private final VideoCacheRx videoCache;
    @NonNull private final VideoServiceWrapper videoServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public PaginatedVideoCacheRx(
            @NonNull VideoCacheRx videoCache,
            @NonNull VideoServiceWrapper videoServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.videoCache = videoCache;
        this.videoServiceWrapper = videoServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<PaginatedVideoDTO> fetch(@NonNull VideoCategoryId key)
    {
        return videoServiceWrapper.getVideosRx(key);
    }

    @Override public void onNext(@NonNull VideoCategoryId key, @NonNull PaginatedVideoDTO value)
    {
        List<VideoDTO> data = value.getData();
        if (data != null)
        {
            videoCache.onNext(data);
        }
        super.onNext(key, value);
    }
}
