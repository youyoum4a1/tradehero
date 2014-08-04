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
        int level = (currentXP / 100) + 1;
        int base = (level - 1) * 100;
        int max = level * 100;

        return new LevelDTO(level, base, max);
    }

    public LevelDTO getNextLevelDTO(int currentLevel)
    {
        int level = currentLevel + 1;
        int base = (level - 1) * 100;
        int max = level * 100;
        return new LevelDTO(level, base, max);
    }
}
