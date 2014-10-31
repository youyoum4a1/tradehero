package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.achievement.AchievementDefDTO;
import com.tradehero.th.api.achievement.key.AchievementDefId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache
public class AchievementDefCache extends StraightDTOCacheNew<AchievementDefId, AchievementDefDTO>
{
    private static final int DEFAULT_SIZE = 50;
    private final AchievementServiceWrapper achievementServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public AchievementDefCache(
            @NotNull AchievementServiceWrapper achievementServiceWrapper,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_SIZE, dtoCacheUtil);
        this.achievementServiceWrapper = achievementServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override public AchievementDefDTO fetch(@NotNull AchievementDefId key) throws Throwable
    {
        return null;
    }
}
