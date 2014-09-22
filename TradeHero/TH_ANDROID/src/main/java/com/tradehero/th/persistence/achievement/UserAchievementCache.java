package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import com.tradehero.th.utils.broadcast.BroadcastTaskNew;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserAchievementCache extends StraightDTOCacheNew<UserAchievementId, UserAchievementDTO>
{
    public static final int DEFAULT_SIZE = 20;

    @NotNull private final AchievementServiceWrapper achievementServiceWrapper;
    @NotNull private final BroadcastUtils broadcastUtils;

    @Inject public UserAchievementCache(@NotNull AchievementServiceWrapper achievementServiceWrapper,
            @NotNull BroadcastUtils broadcastUtils)
    {
        super(DEFAULT_SIZE);
        this.achievementServiceWrapper = achievementServiceWrapper;
        this.broadcastUtils = broadcastUtils;
    }

    @NotNull @Override public UserAchievementDTO fetch(@NotNull UserAchievementId key) throws Throwable
    {
        return achievementServiceWrapper.getUserAchievementDetails(key);
    }

    @Nullable public UserAchievementDTO pop(@NotNull UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = get(userAchievementId);
        if (userAchievementDTO != null)
        {
            remove(userAchievementId);
        }
        return userAchievementDTO;
    }

    public void remove(@NotNull UserAchievementId userAchievementId)
    {
        invalidate(userAchievementId);
    }

    public boolean shouldShow(@NotNull UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = get(userAchievementId);
        return userAchievementDTO != null &&
                !userAchievementDTO.shouldShow();
    }

    public void put(@NotNull List<? extends UserAchievementDTO> userAchievementDTOs)
    {
        for (UserAchievementDTO userAchievementDTO : userAchievementDTOs)
        {
            putAndBroadcast(userAchievementDTO);
        }
    }

    public BroadcastTaskNew putAndBroadcast(@NotNull UserAchievementDTO userAchievementDTO)
    {
        put(userAchievementDTO.getUserAchievementId(), userAchievementDTO);
        final UserAchievementId userAchievementId = userAchievementDTO.getUserAchievementId();
        return broadcastUtils.enqueue(userAchievementId);
    }
}
