package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.UserAchievementId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class UserAchievementCache extends StraightDTOCacheNew<UserAchievementId, UserAchievementDTO>
{
    //TODO implements CutDTO when AchievementsDTO has its own cache?

    public static final int DEFAULT_SIZE = 20;

    private final AchievementServiceWrapper achievementServiceWrapper;

    @Inject public UserAchievementCache(AchievementServiceWrapper achievementServiceWrapper)
    {
        super(DEFAULT_SIZE);
        this.achievementServiceWrapper = achievementServiceWrapper;
    }

    @NotNull @Override public UserAchievementDTO fetch(@NotNull UserAchievementId key) throws Throwable
    {
        return achievementServiceWrapper.getUserAchievementDetails(key);
    }
}
