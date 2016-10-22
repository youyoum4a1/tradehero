package com.androidth.general.persistence.position;

import android.support.annotation.NonNull;

import com.androidth.general.api.position.LiveOwnedPositionId;
import com.androidth.general.api.position.PositionCompactId;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.SystemCache;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @SystemCache
public class LivePositionCompactIdCacheRx extends BaseDTOCacheRx<PositionCompactId, LiveOwnedPositionId>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 2000;

    //<editor-fold desc="Constructors">
    @Inject public LivePositionCompactIdCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>
}
