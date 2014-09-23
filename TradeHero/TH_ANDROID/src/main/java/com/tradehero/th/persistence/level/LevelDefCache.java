package com.tradehero.th.persistence.level;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.LevelDefIdList;
import com.tradehero.th.api.level.key.LevelDefId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class LevelDefCache extends StraightDTOCacheNew<LevelDefId, LevelDefDTO>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    //<editor-fold desc="Constructors">
    @Inject public LevelDefCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @NotNull @Override public LevelDefDTO fetch(@NotNull LevelDefId key) throws Throwable
    {
        throw new RuntimeException();
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public LevelDefDTOList put(@Nullable List<LevelDefDTO> levelDefDTOList)
    {
        if (levelDefDTOList == null)
        {
            return null;
        }
        LevelDefDTOList previous = new LevelDefDTOList();
        for (LevelDefDTO levelDefDTO : levelDefDTOList)
        {
            previous.add(put(levelDefDTO.getId(), levelDefDTO));
        }
        return previous;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public LevelDefDTOList get(@Nullable LevelDefIdList levelDefIdList)
    {
        if (levelDefIdList == null)
        {
            return null;
        }

        LevelDefDTOList levelDefDTOs = new LevelDefDTOList();
        for (LevelDefId levelDefId : levelDefIdList)
        {
            levelDefDTOs.add(get(levelDefId));
        }
        return levelDefDTOs;
    }
}
