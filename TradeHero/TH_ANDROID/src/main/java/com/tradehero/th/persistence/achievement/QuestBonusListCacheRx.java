package com.ayondo.academy.persistence.achievement;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.achievement.QuestBonusDTOList;
import com.ayondo.academy.api.achievement.key.QuestBonusListId;
import com.ayondo.academy.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class QuestBonusListCacheRx extends BaseFetchDTOCacheRx<QuestBonusListId, QuestBonusDTOList>
{
    private static final int DEFAULT_VALUE_SIZE = 1;
    private final AchievementServiceWrapper achievementServiceWrapper;
    private final QuestBonusCacheRx questBonusCache;

    //<editor-fold desc="Constructors">
    @Inject public QuestBonusListCacheRx(
            @NonNull AchievementServiceWrapper achievementServiceWrapper,
            @NonNull QuestBonusCacheRx questBonusCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, dtoCacheUtil);
        this.achievementServiceWrapper = achievementServiceWrapper;
        this.questBonusCache = questBonusCache;
    }
    //</editor-fold>

    @NonNull @Override public Observable<QuestBonusDTOList> fetch(@NonNull QuestBonusListId key)
    {
        return achievementServiceWrapper.getQuestBonusesRx(key);
    }

    @Override public void onNext(@NonNull QuestBonusListId key, @NonNull QuestBonusDTOList value)
    {
        questBonusCache.onNext(value);
    }
}
