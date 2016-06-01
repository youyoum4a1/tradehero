package com.ayondo.academy.persistence.achievement;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.achievement.QuestBonusDTO;
import com.ayondo.academy.api.achievement.QuestBonusDTOList;
import com.ayondo.academy.api.achievement.key.QuestBonusId;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class QuestBonusCacheRx extends BaseDTOCacheRx<QuestBonusId, QuestBonusDTO>
{
    private static final int DEFAULT_VALUE_SIZE = 50;

    //<editor-fold desc="Constructors">
    @Inject public QuestBonusCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
    super(DEFAULT_VALUE_SIZE, dtoCacheUtil);
}
    //</editor-fold>

    public void onNext(@NonNull QuestBonusDTOList value)
    {
        for (QuestBonusDTO questBonusDTO : value)
        {
            onNext(questBonusDTO.getQuestBonusId(), questBonusDTO);
        }
    }
}
