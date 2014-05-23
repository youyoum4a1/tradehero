package com.tradehero.th.api.alert;

import java.util.Date;

public class AlertEventDTO
{
    public int id;
    public Date triggeredAt;
    public double securityPrice;

    public AlertEventDTO()
    {
    }
}
