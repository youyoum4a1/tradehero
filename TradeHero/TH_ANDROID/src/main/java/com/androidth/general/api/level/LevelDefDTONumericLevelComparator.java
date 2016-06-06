package com.androidth.general.api.level;

import java.io.Serializable;
import java.util.Comparator;

public class LevelDefDTONumericLevelComparator implements Comparator<LevelDefDTO>, Serializable
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
