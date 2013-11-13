package com.tradehero.th.api.alert;

import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 11/13/13 Time: 12:53 PM To change this template use File | Settings | File Templates. */
public class AlertEventDTO
{
    public static final String TAG = AlertEventDTO.class.getSimpleName();

    public int id;
    public Date triggeredAt;
    public double securityPrice;

    public AlertEventDTO()
    {
    }
}
