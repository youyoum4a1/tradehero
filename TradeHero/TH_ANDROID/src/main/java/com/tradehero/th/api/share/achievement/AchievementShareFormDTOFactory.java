package com.tradehero.th.api.share.achievement;

import com.tradehero.th.api.achievement.AchievementShareRequestDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.models.share.ShareDestinationWithEnum;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class AchievementShareFormDTOFactory
{
    @Inject public AchievementShareFormDTOFactory()
    {
        super();
    }

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
        timelineItemShareFormDTO.achievementShareRequestDTO = new AchievementShareRequestDTO(shareDestinationWithEnums);
        timelineItemShareFormDTO.userAchievementId = userAchievementDTO.getUserAchievementId();
    }
}