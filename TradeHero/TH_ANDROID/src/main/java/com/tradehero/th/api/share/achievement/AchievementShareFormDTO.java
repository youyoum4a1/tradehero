package com.tradehero.th.api.share.achievement;

import android.support.annotation.NonNull;
import com.tradehero.th.api.achievement.AchievementShareReqFormDTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.social.HasSocialNetworkEnumList;
import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.List;

public class AchievementShareFormDTO implements SocialShareFormDTO, HasSocialNetworkEnumList
{
    @NonNull public UserAchievementId userAchievementId;
    @NonNull public AchievementShareReqFormDTO achievementShareReqFormDTO;

    //<editor-fold desc="Constructors">
    AchievementShareFormDTO()
    {
    }

    public AchievementShareFormDTO(
            @NonNull UserAchievementId userAchievementId,
            @NonNull AchievementShareReqFormDTO achievementShareReqFormDTO)
    {
        this.userAchievementId = userAchievementId;
        this.achievementShareReqFormDTO = achievementShareReqFormDTO;
    }
    //</editor-fold>

    @NonNull @Override public List<SocialNetworkEnum> getSocialNetworkEnumList()
    {
        return achievementShareReqFormDTO.getSocialNetworkEnumList();
    }
}
