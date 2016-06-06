package com.androidth.general.api.achievement.key;

import com.androidth.general.common.persistence.DTOKey;

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
