package com.androidth.general.persistence.education;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.education.VideoCategoryDTO;
import com.androidth.general.api.education.VideoCategoryId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class VideoCategoryCacheRx extends BaseDTOCacheRx<VideoCategoryId, VideoCategoryDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 100;

    //<editor-fold desc="Constructors">
    @Inject public VideoCategoryCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull List<? extends VideoCategoryDTO> videoCategoryDTOs)
    {
        for(VideoCategoryDTO category : videoCategoryDTOs)
        {
            onNext(category.getVideoCategoryId(), category);
        }
    }
}
