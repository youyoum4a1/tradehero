package com.ayondo.academy.persistence.level;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.level.LevelDefDTO;
import com.ayondo.academy.api.level.key.LevelDefId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class LevelDefCacheRx extends BaseDTOCacheRx<LevelDefId, LevelDefDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;

    //<editor-fold desc="Constructors">
    @Inject public LevelDefCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull List<LevelDefDTO> levelDefDTOList)
    {
        for (LevelDefDTO levelDefDTO : levelDefDTOList)
        {
            onNext(levelDefDTO.getId(), levelDefDTO);
        }
    }
}
