package com.tradehero.th.persistence.achievement;

import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import com.tradehero.th.utils.achievement.AchievementModule;
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
    @NotNull private final LocalBroadcastManager localBroadcastManager;

    @Inject public UserAchievementCache(@NotNull AchievementServiceWrapper achievementServiceWrapper,
            @NotNull LocalBroadcastManager localBroadcastManager)
    {
        super(DEFAULT_SIZE);
        this.achievementServiceWrapper = achievementServiceWrapper;
        this.localBroadcastManager = localBroadcastManager;
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

    public BroadcastUtils putAndBroadcast(@NotNull UserAchievementDTO userAchievementDTO)
    {
        put(userAchievementDTO.getUserAchievementId(), userAchievementDTO);
        final UserAchievementId userAchievementId = userAchievementDTO.getUserAchievementId();
        BroadcastUtils broadcastTask = new BroadcastUtils(userAchievementId, localBroadcastManager, AchievementModule.ACHIEVEMENT_INTENT_ACTION_NAME, AchievementModule.KEY_USER_ACHIEVEMENT_ID);
        broadcastTask.start();
        return broadcastTask;
    }
}
