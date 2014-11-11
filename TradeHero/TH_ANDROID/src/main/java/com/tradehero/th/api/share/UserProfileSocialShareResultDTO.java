package com.tradehero.th.api.share;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserProfileDTO;

public class UserProfileSocialShareResultDTO implements SocialShareResultDTO
{
    @NonNull public final UserProfileDTO userProfileDTO;

    //<editor-fold desc="Constructors">
    public UserProfileSocialShareResultDTO(@NonNull UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
    }
    //</editor-fold>
}
