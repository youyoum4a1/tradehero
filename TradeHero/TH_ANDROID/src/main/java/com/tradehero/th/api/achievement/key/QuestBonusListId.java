package com.ayondo.academy.api.achievement.key;

import com.tradehero.common.persistence.DTOKey;

public class QuestBonusListId implements DTOKey
{
    @Override public int hashCode()
    {
        return 0;
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof QuestBonusListId;
    }
}
