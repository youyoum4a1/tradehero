package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SecurityAlertCountingHelper
{
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final AlertCompactListCache alertCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityAlertCountingHelper(
            @NotNull UserProfileCache userProfileCache,
            @NotNull AlertCompactListCache alertCompactListCache)
    {
        this.userProfileCache = userProfileCache;
        this.alertCompactListCache = alertCompactListCache;
    }
    //</editor-fold>

    public AlertSlotDTO getAlertSlots(UserBaseKey userBaseKey)
    {
        UserProfileDTO userProfile = userProfileCache.get(userBaseKey);
        AlertCompactDTOList alertCompactDTOs = alertCompactListCache.get(userBaseKey);

        AlertSlotDTO alertSlots = new AlertSlotDTO();

        alertSlots.usedAlertSlots = alertCompactDTOs == null ? 0 : alertCompactDTOs.size();
        alertSlots.totalAlertSlots = userProfile == null ? 0 : userProfile.getUserAlertPlansAlertCount();
        alertSlots.freeAlertSlots = alertSlots.totalAlertSlots - alertSlots.usedAlertSlots;
        return alertSlots;
    }
}
