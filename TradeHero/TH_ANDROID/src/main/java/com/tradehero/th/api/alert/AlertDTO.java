package com.tradehero.th.api.alert;

import java.util.List;


public class AlertDTO extends AlertCompactDTO
{
    public static final String TAG = AlertDTO.class.getSimpleName();

    public List<AlertEventDTO> alertEvents;

    public AlertDTO()
    {
    }
}
