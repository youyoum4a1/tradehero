package com.androidth.general.persistence.achievement;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.achievement.AchievementCategoryDTO;
import com.androidth.general.api.achievement.key.AchievementCategoryId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.network.service.AchievementServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class AchievementCategoryCacheRx extends BaseFetchDTOCacheRx<AchievementCategoryId, AchievementCategoryDTO>
{
    private static final int DEFAULT_VALUE_SIZE = 50;
    private final AchievementServiceWrapper achievementServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public AchievementCategoryCacheRx(
            @NonNull AchievementServiceWrapper achievementServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, dtoCacheUtil);
        this.achievementServiceWrapper = achievementServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override public Observable<AchievementCategoryDTO> fetch(@NonNull AchievementCategoryId key)
    {
        return achievementServiceWrapper.getAchievementCategoryRx(key);
    }

    public void onNext(@NonNull UserBaseKey userBaseKey, @Nullable List<AchievementCategoryDTO> value)
    {
        if (value != null)
        {
            for (AchievementCategoryDTO achievementCategoryDTO : value)
            {
                onNext(achievementCategoryDTO.getCategoryId(userBaseKey), achievementCategoryDTO);
            }
        }
    }
}
