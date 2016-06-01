package com.ayondo.academy.persistence.position;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.position.PositionCompactId;
import com.ayondo.academy.api.position.PositionDTOCompact;
import com.ayondo.academy.api.position.PositionDTOCompactList;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class PositionCompactCacheRx extends BaseDTOCacheRx<PositionCompactId, PositionDTOCompact>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public PositionCompactCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull PositionDTOCompactList values)
    {
        for (PositionDTOCompact value: values)
        {
            onNext(value.getPositionCompactId(), value);
        }
    }
}
