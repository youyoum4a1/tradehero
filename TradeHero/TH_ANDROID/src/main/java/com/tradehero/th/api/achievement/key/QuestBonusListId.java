package com.tradehero.th.api.achievement.key;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.achievement.QuestBonusDTOList;

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
