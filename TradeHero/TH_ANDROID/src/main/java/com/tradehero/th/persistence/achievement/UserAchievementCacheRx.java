package com.ayondo.academy.persistence.achievement;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.achievement.AchievementDefDTO;
import com.ayondo.academy.api.achievement.UserAchievementDTO;
import com.ayondo.academy.api.achievement.key.UserAchievementId;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.network.service.AchievementServiceWrapper;
import com.ayondo.academy.persistence.portfolio.PortfolioCompactListCacheRx;
import com.ayondo.academy.utils.broadcast.BroadcastTaskNew;
import com.ayondo.academy.utils.broadcast.BroadcastUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import timber.log.Timber;

@Singleton @UserCache
public class UserAchievementCacheRx extends BaseFetchDTOCacheRx<UserAchievementId, UserAchievementDTO>
{
    public static final int DEFAULT_VALUE_SIZE = 20;

    @NonNull private final AchievementServiceWrapper achievementServiceWrapper;
    @NonNull private final BroadcastUtils broadcastUtils;
    @NonNull private final Lazy<CurrentUserId> currentUserId;
    @NonNull private final Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public UserAchievementCacheRx(
            @NonNull AchievementServiceWrapper achievementServiceWrapper,
            @NonNull BroadcastUtils broadcastUtils,
            @NonNull Lazy<CurrentUserId> currentUserId,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, dtoCacheUtil);
        this.achievementServiceWrapper = achievementServiceWrapper;
        this.broadcastUtils = broadcastUtils;
        this.currentUserId = currentUserId;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<UserAchievementDTO> fetch(@NonNull UserAchievementId key)
    {
        return achievementServiceWrapper.getUserAchievementDetailsRx(key);
    }

    @Nullable public UserAchievementDTO pop(@NonNull UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = getCachedValue(userAchievementId);
        if (userAchievementDTO != null)
        {
            invalidate(userAchievementId);
        }
        return userAchievementDTO;
    }

    public boolean shouldShow(@NonNull UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = getCachedValue(userAchievementId);
        return userAchievementDTO != null &&
                !userAchievementDTO.shouldShow();
    }

    public void onNextNonDefDuplicates(@NonNull List<? extends UserAchievementDTO> userAchievementDTOs)
    {
        for (UserAchievementDTO userAchievementDTO : userAchievementDTOs)
        {
            if (!isDuplicateDef(userAchievementDTO))
            {
                onNextAndBroadcast(userAchievementDTO);
            }
            else
            {
                Timber.d("Found duplicate userAchievementDTO %s", userAchievementDTO);
            }
        }
    }

    public BroadcastTaskNew onNextAndBroadcast(@NonNull UserAchievementDTO userAchievementDTO)
    {
        onNext(userAchievementDTO.getUserAchievementId(), userAchievementDTO);
        final UserAchievementId userAchievementId = userAchievementDTO.getUserAchievementId();
        clearPortfolioCaches(userAchievementDTO.achievementDef);
        return broadcastUtils.enqueue(userAchievementId);
    }

    public boolean isDuplicateDef(@NonNull UserAchievementDTO userAchievementDTO)
    {
        for (UserAchievementDTO cachedValue: new ArrayList<>(snapshot().values()))
        {
            if (userAchievementDTO.isSameDefId(cachedValue))
            {
                return true;
            }
        }
        return false;
    }

    public void clearPortfolioCaches(@NonNull AchievementDefDTO achievementDefDTO)
    {
        if (achievementDefDTO.virtualDollars != 0)
        {
            portfolioCompactListCache.get().get(currentUserId.get().toUserBaseKey());
        }
    }
}
