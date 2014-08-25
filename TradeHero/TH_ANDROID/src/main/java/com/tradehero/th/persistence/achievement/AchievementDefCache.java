package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.achievement.AchievementDefDTO;
import com.tradehero.th.api.achievement.AchievementDefId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class AchievementDefCache extends StraightDTOCacheNew<AchievementDefId, AchievementDefDTO>
{
    private static final int DEFAULT_SIZE = 50;
    private final AchievementServiceWrapper achievementServiceWrapper;

    @Inject public AchievementDefCache(AchievementServiceWrapper achievementServiceWrapper)
    {
        super(DEFAULT_SIZE);
        this.achievementServiceWrapper = achievementServiceWrapper;
    }

    @NotNull @Override public AchievementDefDTO fetch(@NotNull AchievementDefId key) throws Throwable
    {
        return null;
    }
}
