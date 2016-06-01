package com.ayondo.academy.persistence.education;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.education.VideoDTO;
import com.ayondo.academy.api.education.VideoId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class VideoCacheRx extends BaseDTOCacheRx<VideoId, VideoDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 300;

    //<editor-fold desc="Constructors">
    @Inject public VideoCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull List<? extends VideoDTO> videoDTOs)
    {
        for (VideoDTO videoDTO : videoDTOs)
        {
            onNext(videoDTO.getVideoId(), videoDTO);
        }
    }
}
