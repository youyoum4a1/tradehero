package com.tradehero.th.persistence.level;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.key.LevelDefId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache
public class LevelDefCacheRx extends BaseDTOCacheRx<LevelDefId, LevelDefDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 101;

    //<editor-fold desc="Constructors">
    @Inject public LevelDefCacheRx(@NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NotNull List<LevelDefDTO> levelDefDTOList)
    {
        for (LevelDefDTO levelDefDTO : levelDefDTOList)
        {
            onNext(levelDefDTO.getId(), levelDefDTO);
        }
    }
}
