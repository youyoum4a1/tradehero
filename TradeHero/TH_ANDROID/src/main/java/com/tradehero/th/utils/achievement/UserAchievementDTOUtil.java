package com.tradehero.th.utils.achievement;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.UserAchievementId;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserAchievementDTOUtil
{
    public static final String INTENT_ACTION_NAME = "com.tradehero.th.achievement.ALERT";
    public static final String KEY_ACHIEVEMENT_NODE = "achievements";

    @NotNull private final UserAchievementCache userAchievementCache;
    @NotNull private final LocalBroadcastManager localBroadcastManager;

    @Inject public UserAchievementDTOUtil(
            @NotNull UserAchievementCache userAchievementCache,
            @NotNull LocalBroadcastManager localBroadcastManager)
    {
        super();
        this.userAchievementCache = userAchievementCache;
        this.localBroadcastManager = localBroadcastManager;
    }

    public boolean shouldShow(@NotNull UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = get(userAchievementId);
        return userAchievementDTO != null &&
                !userAchievementDTO.shouldShow();
    }

    @Nullable public UserAchievementDTO pop(@NotNull UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = userAchievementCache.get(userAchievementId);
        if (userAchievementDTO != null)
        {
            remove(userAchievementId);
        }
        return userAchievementDTO;
    }

    @Nullable public UserAchievementDTO get(@NotNull UserAchievementId userAchievementId)
    {
        return userAchievementCache.get(userAchievementId);
    }

    public void remove(@NotNull UserAchievementId userAchievementId)
    {
        userAchievementCache.invalidate(userAchievementId);
    }

    public void put(@NotNull List<? extends UserAchievementDTO> userAchievementDTOs)
    {
        for (UserAchievementDTO userAchievementDTO : userAchievementDTOs)
        {
            put(userAchievementDTO);
        }
    }

    public void put(@NotNull UserAchievementDTO userAchievementDTO)
    {
        userAchievementCache.put(userAchievementDTO.getUserAchievementId(), userAchievementDTO);
        Intent i = new Intent(INTENT_ACTION_NAME);
        i.putExtra(UserAchievementDTO.class.getName(), userAchievementDTO.getUserAchievementId().getArgs());
        localBroadcastManager.sendBroadcastSync(i);
    }
}
