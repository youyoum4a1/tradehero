package com.androidth.general.api.share.achievement;

import android.support.annotation.NonNull;
import com.androidth.general.api.achievement.key.UserAchievementId;
import com.androidth.general.api.share.SocialShareFormDTO;
import com.androidth.general.api.social.HasSocialNetworkEnumList;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.social.SocialShareReqFormDTO;
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
