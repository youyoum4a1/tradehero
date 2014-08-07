package com.tradehero.th.models.level;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.persistence.level.LevelDefListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LevelDefUtil
{
    private LevelDefListCache levelDefListCache;
    private LevelDefDTOList levelDefDTOList;

    private final LevelDefListId levelDefListId = new LevelDefListId();

    @Inject public LevelDefUtil(LevelDefListCache levelDefListCache)
    {
        super();
        this.levelDefListCache = levelDefListCache;
        this.levelDefListCache.register(levelDefListId, createLevelDefListener());
        this.levelDefListCache.getOrFetchAsync(levelDefListId);
    }

    public void forceUpdate()
    {
        this.levelDefListCache.getOrFetchAsync(levelDefListId, true);
    }

    @NotNull public LevelDefDTO getCurrentLevel(int currentXP)
    {
        //TODO get from server/ cache
        int level = (currentXP / 100) + 1;
        int base = (level - 1) * 100;
        int max = level * 100;

        LevelDefDTO levelDefDTO = new LevelDefDTO();
        levelDefDTO.level = level;
        levelDefDTO.xpFrom = base;
        levelDefDTO.xpTo = max;

        return levelDefDTO;
    }

    public LevelDefDTO getNextLevelDTO(int currentLevel)
    {
        int level = currentLevel + 1;
        int base = (level - 1) * 100;
        int max = level * 100;

        LevelDefDTO levelDefDTO = new LevelDefDTO();
        levelDefDTO.level = level;
        levelDefDTO.xpFrom = base;
        levelDefDTO.xpTo = max;

        return levelDefDTO;
    }

    private void setLevelDefDTOList(LevelDefDTOList levelDefDTOList)
    {
        this.levelDefDTOList = levelDefDTOList;
    }

    private LevelDefCacheListener createLevelDefListener()
    {
        return new LevelDefCacheListener();
    }

    private class LevelDefCacheListener implements DTOCacheNew.Listener<LevelDefListId, LevelDefDTOList>
    {

        @Override public void onDTOReceived(@NotNull LevelDefListId key, @NotNull LevelDefDTOList value)
        {
            setLevelDefDTOList(levelDefDTOList);
        }

        @Override public void onErrorThrown(@NotNull LevelDefListId key, @NotNull Throwable error)
        {

        }
    }
}
