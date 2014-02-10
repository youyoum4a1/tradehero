package com.tradehero.th.models.alert;

/**
 * Created by xavier on 2/10/14.
 */
public class AlertSlotDTO
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
