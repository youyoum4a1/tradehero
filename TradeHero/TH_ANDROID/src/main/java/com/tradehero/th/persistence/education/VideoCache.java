package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.education.VideoDTO;
import com.tradehero.th.api.education.VideoId;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class VideoCache extends StraightDTOCacheNew<VideoId, VideoDTO>
{
    private static final int DEFAULT_MAX_SIZE = 300;

    //<editor-fold desc="Constructors">
    @Inject public VideoCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @NotNull @Override public VideoDTO fetch(@NotNull VideoId key) throws Throwable
    {
        throw new IllegalArgumentException("No fetch on this cache");
    }
}
