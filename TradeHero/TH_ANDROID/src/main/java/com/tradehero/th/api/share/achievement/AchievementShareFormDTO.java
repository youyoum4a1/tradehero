package com.ayondo.academy.api.share.achievement;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.achievement.key.UserAchievementId;
import com.ayondo.academy.api.share.SocialShareFormDTO;
import com.ayondo.academy.api.social.HasSocialNetworkEnumList;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.api.social.SocialShareReqFormDTO;
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
