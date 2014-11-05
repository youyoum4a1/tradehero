package com.tradehero.th.persistence.education;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.education.VideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache
public class VideoCategoryCacheRx extends BaseDTOCacheRx<VideoCategoryId, VideoCategoryDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 100;
    private static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    //<editor-fold desc="Constructors">
    @Inject public VideoCategoryCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
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
