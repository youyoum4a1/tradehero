package com.tradehero.th.api.alert;

import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/13/13 Time: 12:52 PM To change this template use File | Settings | File Templates. */
public class AlertDTO extends AlertCompactDTO
{
    public static final String TAG = AlertDTO.class.getSimpleName();

    public List<AlertEventDTO> alertEvents;

    public AlertDTO()
    {
    }
}
