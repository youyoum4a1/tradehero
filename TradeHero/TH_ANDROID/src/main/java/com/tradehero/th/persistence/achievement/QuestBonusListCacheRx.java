package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.key.QuestBonusListId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import rx.Observable;

@Singleton @UserCache
public class QuestBonusListCacheRx extends BaseFetchDTOCacheRx<QuestBonusListId, QuestBonusDTOList>
{
    private static final int DEFAULT_VALUE_SIZE = 1;
    private static final int DEFAULT_SUBJECT_SIZE = 1;
    private final AchievementServiceWrapper achievementServiceWrapper;
    private final QuestBonusCacheRx questBonusCache;

    //<editor-fold desc="Constructors">
    @Inject public QuestBonusListCacheRx(
            @NonNull AchievementServiceWrapper achievementServiceWrapper,
            @NonNull QuestBonusCacheRx questBonusCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, DEFAULT_SUBJECT_SIZE, DEFAULT_SUBJECT_SIZE, dtoCacheUtil);
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
