package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.education.VideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryId;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class VideoCategoryCache extends StraightDTOCacheNew<VideoCategoryId, VideoCategoryDTO>
{
    private static final int DEFAULT_MAX_SIZE = 100;

    //<editor-fold desc="Constructors">
    @Inject public VideoCategoryCache(@NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    @NotNull @Override public VideoCategoryDTO fetch(@NotNull VideoCategoryId key) throws Throwable
    {
        throw new IllegalArgumentException("No fetch on this cache");
    }
}
