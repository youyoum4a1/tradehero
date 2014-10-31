package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.achievement.AchievementDefDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.utils.broadcast.BroadcastTaskNew;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import timber.log.Timber;

@Singleton @UserCache
public class UserAchievementCacheRx extends BaseFetchDTOCacheRx<UserAchievementId, UserAchievementDTO>
{
    public static final int DEFAULT_VALUE_SIZE = 20;
    public static final int DEFAULT_SUBJECT_SIZE = 2;

    @NotNull private final AchievementServiceWrapper achievementServiceWrapper;
    @NotNull private final BroadcastUtils broadcastUtils;
    @NotNull private final Lazy<CurrentUserId> currentUserId;
    @NotNull private final Lazy<PortfolioCompactListCache> portfolioCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public UserAchievementCacheRx(
            @NotNull AchievementServiceWrapper achievementServiceWrapper,
            @NotNull BroadcastUtils broadcastUtils,
            @NotNull Lazy<CurrentUserId> currentUserId,
            @NotNull Lazy<PortfolioCompactListCache> portfolioCompactListCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, DEFAULT_SUBJECT_SIZE, DEFAULT_SUBJECT_SIZE, dtoCacheUtil);
        this.achievementServiceWrapper = achievementServiceWrapper;
        this.broadcastUtils = broadcastUtils;
        this.currentUserId = currentUserId;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }
    //</editor-fold>

    @NotNull @Override protected Observable<UserAchievementDTO> fetch(@NotNull UserAchievementId key)
    {
        return achievementServiceWrapper.getUserAchievementDetailsRx(key);
    }

    @Nullable public UserAchievementDTO pop(@NotNull UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = getValue(userAchievementId);
        if (userAchievementDTO != null)
        {
            invalidate(userAchievementId);
        }
        return userAchievementDTO;
    }

    public boolean shouldShow(@NotNull UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = getValue(userAchievementId);
        return userAchievementDTO != null &&
                !userAchievementDTO.shouldShow();
    }

    public void onNextNonDefDuplicates(@NotNull List<? extends UserAchievementDTO> userAchievementDTOs)
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

    public BroadcastTaskNew onNextAndBroadcast(@NotNull UserAchievementDTO userAchievementDTO)
    {
        onNext(userAchievementDTO.getUserAchievementId(), userAchievementDTO);
        final UserAchievementId userAchievementId = userAchievementDTO.getUserAchievementId();
        clearPortfolioCaches(userAchievementDTO.achievementDef);
        return broadcastUtils.enqueue(userAchievementId);
    }

    public boolean isDuplicateDef(@NotNull UserAchievementDTO userAchievementDTO)
    {
        for (@NotNull UserAchievementDTO cachedValue: new ArrayList<>(snapshot().values()))
        {
            if (userAchievementDTO.isSameDefId(cachedValue))
            {
                return true;
            }
        }
        return false;
    }

    public void clearPortfolioCaches(@NotNull AchievementDefDTO achievementDefDTO)
    {
        if (achievementDefDTO.virtualDollars != 0)
        {
            portfolioCompactListCache.get().getOrFetchAsync(currentUserId.get().toUserBaseKey(), true);
        }
    }
}
