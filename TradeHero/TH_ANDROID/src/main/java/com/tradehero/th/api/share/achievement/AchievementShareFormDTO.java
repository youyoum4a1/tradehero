package com.tradehero.th.api.share.achievement;

import android.support.annotation.NonNull;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.social.HasSocialNetworkEnumList;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialShareReqFormDTO;
import java.util.List;

public class AchievementShareFormDTO implements SocialShareFormDTO, HasSocialNetworkEnumList
{
    @NonNull public UserAchievementId userAchievementId;
    @NonNull public SocialShareReqFormDTO socialShareReqFormDTO;

    //<editor-fold desc="Constructors">
    AchievementShareFormDTO()
    {
    }

    public AchievementShareFormDTO(
            @NonNull UserAchievementId userAchievementId,
            @NonNull SocialShareReqFormDTO socialShareReqFormDTO)
    {
        this.userAchievementId = userAchievementId;
        this.socialShareReqFormDTO = socialShareReqFormDTO;
    }
    //</editor-fold>

    @NonNull @Override public List<SocialNetworkEnum> getSocialNetworkEnumList()
    {
        return socialShareReqFormDTO.getSocialNetworkEnumList();
    }
}
