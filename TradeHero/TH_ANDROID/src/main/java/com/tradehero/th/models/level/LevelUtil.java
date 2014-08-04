package com.tradehero.th.models.level;

import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LevelUtil
{

    @Inject public LevelUtil()
    {
        super();
    }

    @NotNull public LevelDTO getCurrentLevel(int currentXP)
    {
        //TODO get from server
        int level = (currentXP / 10) + 1;
        int base = (level - 1) * 10;
        int max = level * 10;

        return new LevelDTO(level, base, max);
    }

    public LevelDTO getNextLevel(int currentLevel)
    {
        return null;
    }
}
