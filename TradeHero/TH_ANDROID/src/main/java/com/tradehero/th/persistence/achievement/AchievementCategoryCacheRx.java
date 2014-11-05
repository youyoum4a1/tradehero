package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import com.tradehero.th.api.achievement.key.AchievementCategoryId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observable;

@Singleton @UserCache
public class AchievementCategoryCacheRx extends BaseFetchDTOCacheRx<AchievementCategoryId, AchievementCategoryDTO>
{
    private static final int DEFAULT_VALUE_SIZE = 50;
    private static final int DEFAULT_SUBJECT_SIZE = 5;
    private final AchievementServiceWrapper achievementServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public AchievementCategoryCacheRx(
            @NonNull AchievementServiceWrapper achievementServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, DEFAULT_SUBJECT_SIZE, DEFAULT_SUBJECT_SIZE, dtoCacheUtil);
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
