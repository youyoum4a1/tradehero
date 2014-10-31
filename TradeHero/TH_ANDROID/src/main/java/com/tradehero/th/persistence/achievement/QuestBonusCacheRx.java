package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.key.QuestBonusId;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache public class QuestBonusCacheRx extends BaseDTOCacheRx<QuestBonusId, QuestBonusDTO>
{
    private static final int DEFAULT_VALUE_SIZE = 50;
    private static final int DEFAULT_SUBJECT_SIZE = 50;

    //<editor-fold desc="Constructors">
    @Inject public QuestBonusCacheRx(@NotNull DTOCacheUtilRx dtoCacheUtil)
    {
    super(DEFAULT_VALUE_SIZE, DEFAULT_SUBJECT_SIZE, dtoCacheUtil);
}
    //</editor-fold>

    public void onNext(@NotNull QuestBonusDTOList value)
    {
        for (QuestBonusDTO questBonusDTO : value)
        {
            onNext(questBonusDTO.getQuestBonusId(), questBonusDTO);
        }
    }
}
