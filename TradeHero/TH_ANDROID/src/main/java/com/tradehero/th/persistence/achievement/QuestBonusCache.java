package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.key.QuestBonusId;
import com.tradehero.th.api.achievement.key.QuestBonusIdList;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class QuestBonusCache extends StraightDTOCacheNew<QuestBonusId, QuestBonusDTO>
{
    private static final int DEFAULT_SIZE = 50;

    @Inject public QuestBonusCache()
    {
        super(DEFAULT_SIZE);
    }

    @NotNull @Override public QuestBonusDTO fetch(@NotNull QuestBonusId key) throws Throwable
    {
        throw new RuntimeException("Not implemented");
    }

    public void put(@Nullable QuestBonusDTOList value)
    {
        if (value != null)
        {
            for (QuestBonusDTO questBonusDTO : value)
            {
                put(questBonusDTO.getQuestBonusId(), questBonusDTO);
            }
        }
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public QuestBonusDTOList get(@Nullable QuestBonusIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }

        QuestBonusDTOList list = new QuestBonusDTOList();
        for (QuestBonusId questBonusId : cutValue)
        {
            list.add(get(questBonusId));
        }
        return list;
    }
}
