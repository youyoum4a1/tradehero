package com.tradehero.th.models.level;

public class LevelDTO
{
    int currentLevel;
    int baseXp;
    int maxXp;

    public LevelDTO(int currentLevel, int baseXp, int maxXp)
    {
        this.currentLevel = currentLevel;
        this.baseXp = baseXp;
        this.maxXp = maxXp;
    }

    public int getCurrentLevel()
    {
        return currentLevel;
    }

    public int getBaseXp()
    {
        return baseXp;
    }

    public int getMaxXp()
    {
        return maxXp;
    }
}
