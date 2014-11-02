package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.position.PositionCompactId;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOCompactList;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache public class PositionCompactCacheRx extends BaseDTOCacheRx<PositionCompactId, PositionDTOCompact>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    //<editor-fold desc="Constructors">
    @Inject public PositionCompactCacheRx(@NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NotNull PositionDTOCompactList values)
    {
        for (@NotNull PositionDTOCompact value: values)
        {
            onNext(value.getPositionCompactId(), value);
        }
    }
}
