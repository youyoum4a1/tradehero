package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.achievement.AchievementDefDTO;
import com.tradehero.th.api.achievement.key.AchievementDefId;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache
public class AchievementDefCacheRx extends BaseDTOCacheRx<AchievementDefId, AchievementDefDTO>
{
    private static final int DEFAULT_VALUE_SIZE = 50;
    private static final int DEFAULT_SUBJECT_SIZE = 5;

    //<editor-fold desc="Constructors">
    @Inject public AchievementDefCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, DEFAULT_SUBJECT_SIZE, dtoCacheUtil);
    }
    //</editor-fold>
}
