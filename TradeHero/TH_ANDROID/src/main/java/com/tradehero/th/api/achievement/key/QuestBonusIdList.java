package com.tradehero.th.api.achievement.key;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import java.util.Collection;
import org.jetbrains.annotations.Nullable;

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
