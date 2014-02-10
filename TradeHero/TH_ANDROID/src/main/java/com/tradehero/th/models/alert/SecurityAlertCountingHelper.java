package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 2/10/14.
 */
@Singleton public class SecurityAlertCountingHelper
{
    public static final String TAG = SecurityAlertCountingHelper.class.getSimpleName();

    @Inject protected UserProfileCache userProfileCache;
    @Inject protected AlertCompactListCache alertCompactListCache;

    @Inject public SecurityAlertCountingHelper()
    {
    }

    public AlertSlotDTO getAlertSlots(UserBaseKey userBaseKey)
    {
        UserProfileDTO userProfile = userProfileCache.get(userBaseKey);
        AlertIdList alertIds = alertCompactListCache.get(userBaseKey);

        AlertSlotDTO alertSlots = new AlertSlotDTO();

        alertSlots.usedAlertSlots = alertIds == null ? 0 : alertIds.size();
        alertSlots.totalAlertSlots = userProfile == null ? 0 : userProfile.getUserAlertPlansAlertCount();
        alertSlots.freeAlertSlots = alertSlots.totalAlertSlots - alertSlots.usedAlertSlots;
        return alertSlots;
    }
}
