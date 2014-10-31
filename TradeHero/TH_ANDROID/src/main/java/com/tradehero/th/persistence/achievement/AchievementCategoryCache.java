package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.achievement.key.AchievementCategoryId;
import com.tradehero.th.api.achievement.key.AchievementCategoryIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @UserCache
public class AchievementCategoryCache extends StraightDTOCacheNew<AchievementCategoryId, AchievementCategoryDTO>
{
    private static final int DEFAULT_SIZE = 50;
    private final AchievementServiceWrapper achievementServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public AchievementCategoryCache(
            @NotNull AchievementServiceWrapper achievementServiceWrapper,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_SIZE, dtoCacheUtil);
        this.achievementServiceWrapper = achievementServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override public AchievementCategoryDTO fetch(@NotNull AchievementCategoryId key) throws Throwable
    {
        AchievementCategoryDTO achievementCategoryDTO = achievementServiceWrapper.getAchievementCategory(key);
        if(achievementCategoryDTO != null)
        {
            return achievementCategoryDTO;
        }
        throw new THException("Not Found");
    }

    public void put(@NotNull UserBaseKey userBaseKey, @Nullable List<AchievementCategoryDTO> value)
    {
        if (value != null)
        {
            for (AchievementCategoryDTO achievementCategoryDTO : value)
            {
                put(achievementCategoryDTO.getCategoryId(userBaseKey), achievementCategoryDTO);
            }
        }
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public AchievementCategoryDTOList get(@Nullable AchievementCategoryIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }

        AchievementCategoryDTOList list = new AchievementCategoryDTOList();
        for (AchievementCategoryId achievementCategoryId : cutValue)
        {
            list.add(get(achievementCategoryId));
        }
        return list;
    }
}
