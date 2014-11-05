package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.education.VideoDTO;
import com.tradehero.th.api.education.VideoId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache
public class VideoCacheRx extends BaseDTOCacheRx<VideoId, VideoDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 300;
    private static final int DEFAULT_MAX_SUBJECT_SIZE = 3;

    //<editor-fold desc="Constructors">
    @Inject public VideoCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
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
