package com.tradehero.th.api.share.achievement;

import com.tradehero.th.api.achievement.AchievementShareRequestDTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.api.share.SocialShareFormDTO;

public class AchievementShareFormDTO implements SocialShareFormDTO
{
    public UserAchievementId userAchievementId;
    public AchievementShareRequestDTO achievementShareRequestDTO;

    //<editor-fold desc="Constructors">
    public AchievementShareFormDTO()
    {
    }

    public AchievementShareFormDTO(
            UserAchievementId userAchievementId,
            AchievementShareRequestDTO achievementShareRequestDTO)
    {
        this.userAchievementId = userAchievementId;
        this.achievementShareRequestDTO = achievementShareRequestDTO;
    }
    //</editor-fold>
}
