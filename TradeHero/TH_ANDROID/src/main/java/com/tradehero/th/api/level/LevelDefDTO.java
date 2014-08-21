package com.tradehero.th.api.level;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.level.key.LevelDefId;

public class LevelDefDTO implements DTO
{
    public int id;
    public int xpFrom;
    public int xpTo;
    public String name;
    public String badge;
    public int level;

    public LevelDefId getId()
    {
        return new LevelDefId(id);
    }

    public boolean isXPInLevel(int currentXP)
    {
        return xpFrom <= currentXP && xpTo >= currentXP;
    }

    @Override public String toString()
    {
        return "LevelDefDTO{" +
                "id=" + id +
                ", xpFrom=" + xpFrom +
                ", xpTo=" + xpTo +
                ", name='" + name + '\'' +
                ", badge='" + badge + '\'' +
                ", level=" + level +
                '}';
    }
}
