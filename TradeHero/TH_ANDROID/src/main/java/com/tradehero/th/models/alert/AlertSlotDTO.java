package com.tradehero.th.models.alert;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.users.UserProfileDTO;

public class AlertSlotDTO implements DTO
{
    public int totalAlertSlots;
    public int usedAlertSlots;
    public int freeAlertSlots;

    //<editor-fold desc="Constructors">
    public AlertSlotDTO(@NonNull UserProfileDTO userProfileDTO,
            @NonNull AlertCompactDTOList alertCompactDTOs)
    {
        usedAlertSlots = alertCompactDTOs.size();
        totalAlertSlots = userProfileDTO.getUserAlertPlansAlertCount();
        freeAlertSlots = totalAlertSlots - usedAlertSlots;
    }
    //</editor-fold>
}
