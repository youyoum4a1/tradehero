package com.ayondo.academy.persistence.position;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.ayondo.academy.api.position.OwnedPositionId;
import com.ayondo.academy.api.position.PositionCompactId;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @SystemCache
public class PositionCompactIdCacheRx extends BaseDTOCacheRx<PositionCompactId, OwnedPositionId>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 2000;

    //<editor-fold desc="Constructors">
    @Inject public PositionCompactIdCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>
}
