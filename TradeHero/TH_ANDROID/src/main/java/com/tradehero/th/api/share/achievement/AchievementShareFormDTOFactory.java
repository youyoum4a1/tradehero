package com.tradehero.th.api.share.achievement;

import com.tradehero.th.api.achievement.AchievementShareReqFormDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class AchievementShareFormDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public AchievementShareFormDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NotNull public AchievementShareFormDTO createFrom(
            @NotNull List<SocialNetworkEnum> shareDestinationWithEnums,
            @NotNull UserAchievementDTO userAchievementDTO)
    {
        AchievementShareFormDTO timelineItemShareFormDTO = new AchievementShareFormDTO();
        populateWith(timelineItemShareFormDTO, shareDestinationWithEnums, userAchievementDTO);
        return timelineItemShareFormDTO;
    }

    protected void populateWith(
            @NotNull AchievementShareFormDTO timelineItemShareFormDTO,
            @NotNull List<SocialNetworkEnum> shareDestinationWithEnums,
            @NotNull UserAchievementDTO userAchievementDTO)
    {
        timelineItemShareFormDTO.achievementShareReqFormDTO = new AchievementShareReqFormDTO(shareDestinationWithEnums);
        timelineItemShareFormDTO.userAchievementId = userAchievementDTO.getUserAchievementId();
    }
}