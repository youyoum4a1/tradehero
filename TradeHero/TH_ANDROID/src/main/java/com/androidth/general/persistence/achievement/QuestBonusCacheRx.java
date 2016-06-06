package com.androidth.general.persistence.achievement;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.achievement.QuestBonusDTO;
import com.androidth.general.api.achievement.QuestBonusDTOList;
import com.androidth.general.api.achievement.key.QuestBonusId;
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
