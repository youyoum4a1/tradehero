package com.tradehero.th.models.alert;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.UserProfileDTO;

public class AlertSlotDTO implements DTO
{
    public final int totalAlertSlots;
    public final int usedAlertSlots;
    public final int freeAlertSlots;

    //<editor-fold desc="Constructors">
    public AlertSlotDTO(
            @NonNull UserProfileDTO userProfileDTO,
            @NonNull AlertCompactDTOList alertCompactDTOs,
            @NonNull SystemStatusDTO systemStatusDTO)
    {
        usedAlertSlots = alertCompactDTOs.size();
        if (systemStatusDTO.alertsAreFree)
        {
            totalAlertSlots = Integer.MAX_VALUE;
            freeAlertSlots = Integer.MAX_VALUE;
        }
        else
        {
            totalAlertSlots = userProfileDTO.getUserAlertPlansAlertCount();
            freeAlertSlots = totalAlertSlots - usedAlertSlots;
        }
    }
    //</editor-fold>
}
