package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.key.AchievementCategoryIdList;
import com.tradehero.th.api.achievement.key.QuestBonusIdList;
import com.tradehero.th.api.achievement.key.QuestBonusListId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class QuestBonusListCache extends StraightCutDTOCacheNew<QuestBonusListId, QuestBonusDTOList, QuestBonusIdList>
{
    private static final int DEFAULT_SIZE = 1;
    private final AchievementServiceWrapper achievementServiceWrapper;
    private final QuestBonusCache questBonusCache;

    @Inject public QuestBonusListCache(AchievementServiceWrapper achievementServiceWrapper, QuestBonusCache questBonusCache)
    {
        super(DEFAULT_SIZE);
        this.achievementServiceWrapper = achievementServiceWrapper;
        this.questBonusCache = questBonusCache;
    }

    @NotNull @Override public QuestBonusDTOList fetch(@NotNull QuestBonusListId key) throws Throwable
    {
        return achievementServiceWrapper.getQuestBonuses(key);
    }

    @NotNull @Override protected QuestBonusIdList cutValue(@NotNull QuestBonusListId key, @NotNull QuestBonusDTOList value)
    {
        questBonusCache.put(value);
        return new QuestBonusIdList(value);
    }

    @Nullable @Override protected QuestBonusDTOList inflateValue(@NotNull QuestBonusListId key, @Nullable QuestBonusIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        QuestBonusDTOList value = questBonusCache.get(cutValue);
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
