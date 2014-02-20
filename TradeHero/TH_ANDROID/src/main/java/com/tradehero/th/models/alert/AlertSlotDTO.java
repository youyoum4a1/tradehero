package com.tradehero.th.models.alert;

import com.tradehero.common.persistence.DTO;

/**
 * Created by xavier on 2/10/14.
 */
public class AlertSlotDTO implements DTO
{
    public static final String TAG = AlertSlotDTO.class.getSimpleName();

    public int totalAlertSlots;
    public int usedAlertSlots;
    public int freeAlertSlots;

    public AlertSlotDTO()
    {
        super();
    }
}
