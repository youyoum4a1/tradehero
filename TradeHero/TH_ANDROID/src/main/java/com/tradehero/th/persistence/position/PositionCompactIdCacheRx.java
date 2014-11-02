package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionCompactId;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @SystemCache
public class PositionCompactIdCacheRx extends BaseDTOCacheRx<PositionCompactId, OwnedPositionId>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 2000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 20;

    //<editor-fold desc="Constructors">
    @Inject public PositionCompactIdCacheRx(@NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
    }
    //</editor-fold>
}
