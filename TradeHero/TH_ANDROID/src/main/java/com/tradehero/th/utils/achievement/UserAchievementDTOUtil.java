package com.tradehero.th.utils.achievement;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.th.api.achievement.AchievementsDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.UserAchievementId;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserAchievementDTOUtil
{
    public static final String INTENT_ACTION_NAME = "com.tradehero.th.achievement.ALERT";
    public static final String KEY_ACHIEVEMENT_NODE = "achievements";

    @NotNull private final UserAchievementCache userAchievementCache;
    @NotNull private final LocalBroadcastManager localBroadcastManager;

    @Inject public UserAchievementDTOUtil(UserAchievementCache userAchievementCache, LocalBroadcastManager localBroadcastManager)
    {
        super();
        this.userAchievementCache = userAchievementCache;
        this.localBroadcastManager = localBroadcastManager;
    }

    public boolean shouldShow(UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = get(userAchievementId);
        if (userAchievementDTO == null)
        {
            return false;
        }
        if (userAchievementDTO.achievementDef.isQuest && !userAchievementDTO.isReset && userAchievementDTO.contiguousCount == 0)
        {
            return false;
        }
        return true;
    }

    @Nullable public UserAchievementDTO pop(UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = userAchievementCache.get(userAchievementId);
        if (userAchievementDTO != null)
        {
            userAchievementCache.invalidate(userAchievementId);
        }
        return userAchievementDTO;
    }

    @Nullable public UserAchievementDTO get(UserAchievementId userAchievementId)
    {
        return userAchievementCache.get(userAchievementId);
    }

    public void remove(UserAchievementId userAchievementId)
    {
        userAchievementCache.invalidate(userAchievementId);
    }

    public void put(UserAchievementDTO userAchievementDTO)
    {
        userAchievementCache.put(userAchievementDTO.getUserAchievementId(), userAchievementDTO);
        Intent i = new Intent(INTENT_ACTION_NAME);
        i.putExtra(UserAchievementDTO.class.getName(), userAchievementDTO.getUserAchievementId().getArgs());
        localBroadcastManager.sendBroadcastSync(i);
    }

    public static UserAchievementDTO dummy()
    {
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();
        AchievementsDTO achievementsDTO = new AchievementsDTO();

        userAchievementDTO.id = 1;
        achievementsDTO.virtualDollars = 50;
        achievementsDTO.visual =
                "http://fc08.deviantart.net/fs70/f/2013/333/0/0/badge_for_gaming_signature__banner__or_avatar_by_sinner_pwa-d6w26w3.png";
        achievementsDTO.header = "Achievement Unlocked";
        achievementsDTO.thName = "Master Trader I";
        achievementsDTO.text = "You have earned $50.00 TH$";
        achievementsDTO.subText = "Come back tomorrow to earn $50.00 TH$";
        achievementsDTO.hexColor = "4891E4";

        userAchievementDTO.achievementDef = achievementsDTO;
        userAchievementDTO.isReset = true;
        userAchievementDTO.xpEarned = 400;
        userAchievementDTO.xpTotal = 1030;

        return userAchievementDTO;
    }
}
