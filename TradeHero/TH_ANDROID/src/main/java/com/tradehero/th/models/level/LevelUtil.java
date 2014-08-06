package com.tradehero.th.models.level;

import com.tradehero.th.api.level.LevelDefDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LevelUtil
{

    @Inject public LevelUtil()
    {
        super();
    }

    @NotNull public LevelDefDTO getCurrentLevel(int currentXP)
    {
        //TODO get from server
        int level = (currentXP / 100) + 1;
        int base = (level - 1) * 100;
        int max = level * 100;

        LevelDefDTO levelDefDTO =  new LevelDefDTO();
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
        LevelDefDTO levelDefDTO =  new LevelDefDTO();
        levelDefDTO.level = level;
        levelDefDTO.xpFrom = base;
        levelDefDTO.xpTo = max;
        return levelDefDTO;
    }
}
