package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SecurityAlertCountingHelper
{
    @Inject protected UserProfileCache userProfileCache;
    @Inject protected AlertCompactListCache alertCompactListCache;
    @Inject protected Lazy<AlertCompactCache> alertCompactCacheLazy;

    @Inject public SecurityAlertCountingHelper()
    {
    }

    public AlertSlotDTO getAlertSlots(UserBaseKey userBaseKey)
    {
        UserProfileDTO userProfile = userProfileCache.get(userBaseKey);
        AlertIdList alertIds = alertCompactListCache.get(userBaseKey);

        AlertSlotDTO alertSlots = new AlertSlotDTO();
        int activeCount = 0;
        for (AlertId alertId : alertIds)
        {
            AlertCompactDTO alertCompactDTO = alertCompactCacheLazy.get().get(alertId);
            if (alertCompactDTO != null && alertCompactDTO.active)
            {
                activeCount++;
            }
        }
        alertSlots.usedAlertSlots = activeCount;
        alertSlots.totalAlertSlots =
                userProfile == null ? 0 : userProfile.getUserAlertPlansAlertCount();
        alertSlots.freeAlertSlots = alertSlots.totalAlertSlots - alertSlots.usedAlertSlots;
        return alertSlots;
    }
}
