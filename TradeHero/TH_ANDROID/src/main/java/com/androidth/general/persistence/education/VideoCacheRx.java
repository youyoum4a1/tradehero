package com.androidth.general.persistence.education;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.education.VideoDTO;
import com.androidth.general.api.education.VideoId;
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
