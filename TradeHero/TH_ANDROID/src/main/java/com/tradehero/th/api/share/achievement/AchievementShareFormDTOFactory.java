package com.tradehero.th.api.share.achievement;

import android.support.annotation.NonNull;
import com.tradehero.th.api.achievement.AchievementShareReqFormDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.List;
import javax.inject.Inject;

public class AchievementShareFormDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public AchievementShareFormDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull public AchievementShareFormDTO createFrom(
            @NonNull List<SocialNetworkEnum> shareDestinationWithEnums,
            @NonNull UserAchievementDTO userAchievementDTO)
    {
        AchievementShareFormDTO timelineItemShareFormDTO = new AchievementShareFormDTO();
        populateWith(timelineItemShareFormDTO, shareDestinationWithEnums, userAchievementDTO);
        return timelineItemShareFormDTO;
    }

    protected void populateWith(
            @NonNull AchievementShareFormDTO timelineItemShareFormDTO,
            @NonNull List<SocialNetworkEnum> shareDestinationWithEnums,
            @NonNull UserAchievementDTO userAchievementDTO)
    {
        timelineItemShareFormDTO.achievementShareReqFormDTO = new AchievementShareReqFormDTO(shareDestinationWithEnums);
        timelineItemShareFormDTO.userAchievementId = userAchievementDTO.getUserAchievementId();
    }
}