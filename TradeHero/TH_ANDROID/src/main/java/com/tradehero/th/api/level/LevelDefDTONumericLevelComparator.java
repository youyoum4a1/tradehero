package com.tradehero.th.api.level;

import java.util.Comparator;

public class LevelDefDTONumericLevelComparator implements Comparator<LevelDefDTO>
{
    @Override public int compare(LevelDefDTO lhs, LevelDefDTO rhs)
    {
        if(lhs == null || rhs == null)
        {
            return 0;
        }
        return lhs.level - rhs.level;
    }
}
