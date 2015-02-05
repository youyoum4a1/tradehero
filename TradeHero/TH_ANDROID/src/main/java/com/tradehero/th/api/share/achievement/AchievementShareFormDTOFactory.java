package com.tradehero.th.api.share.achievement;

import android.support.annotation.NonNull;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialShareReqFormDTO;
import java.util.List;

public class AchievementShareFormDTOFactory
{
    @NonNull public static AchievementShareFormDTO createFrom(
            @NonNull List<SocialNetworkEnum> shareDestinationWithEnums,
            @NonNull UserAchievementDTO userAchievementDTO)
    {
        AchievementShareFormDTO timelineItemShareFormDTO = new AchievementShareFormDTO();
        populateWith(timelineItemShareFormDTO, shareDestinationWithEnums, userAchievementDTO);
        return timelineItemShareFormDTO;
    }

    public static void populateWith(
            @NonNull AchievementShareFormDTO timelineItemShareFormDTO,
            @NonNull List<SocialNetworkEnum> shareDestinationWithEnums,
            @NonNull UserAchievementDTO userAchievementDTO)
    {
        timelineItemShareFormDTO.socialShareReqFormDTO = new SocialShareReqFormDTO(shareDestinationWithEnums);
        timelineItemShareFormDTO.userAchievementId = userAchievementDTO.getUserAchievementId();
    }
}