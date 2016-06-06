package com.androidth.general.models.alert;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.alert.AlertCompactDTOList;
import com.androidth.general.api.system.SystemStatusDTO;
import com.androidth.general.api.users.UserProfileDTO;

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
