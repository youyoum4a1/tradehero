package com.tradehero.th.models.alert;

import com.tradehero.common.persistence.DTO;

public class AlertSlotDTO implements DTO
{
    public int totalAlertSlots;
    public int usedAlertSlots;
    public int freeAlertSlots;

    public AlertSlotDTO()
    {
        super();
    }
}
