package com.tradehero.th.api.achievement.key;

import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import java.util.Collection;

public class QuestBonusIdList extends DTOKeyIdList<QuestBonusId>
{
    public QuestBonusIdList(@Nullable Collection<? extends QuestBonusDTO> collection )
    {
        if(collection != null)
        {
            for (QuestBonusDTO dto : collection)
            {
                add(dto.getQuestBonusId());
            }
        }
    }
}
