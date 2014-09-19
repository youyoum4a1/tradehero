package com.tradehero.th.api.share.achievement;

import com.tradehero.th.api.achievement.AchievementShareReqFormDTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.api.share.SocialShareFormDTO;

public class AchievementShareFormDTO implements SocialShareFormDTO
{
    public UserAchievementId userAchievementId;
    public AchievementShareReqFormDTO achievementShareReqFormDTO;

    //<editor-fold desc="Constructors">
    public AchievementShareFormDTO()
    {
    }

    public AchievementShareFormDTO(
            UserAchievementId userAchievementId,
            AchievementShareReqFormDTO achievementShareReqFormDTO)
    {
        this.userAchievementId = userAchievementId;
        this.achievementShareReqFormDTO = achievementShareReqFormDTO;
    }
    //</editor-fold>
}
