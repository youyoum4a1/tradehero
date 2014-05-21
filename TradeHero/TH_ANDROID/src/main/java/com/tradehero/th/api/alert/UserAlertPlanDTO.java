package com.tradehero.th.api.alert;

import java.util.Date;

public class UserAlertPlanDTO
{
    public Date paidUpToUtc;
    public boolean autoRenewingActive;
    public AlertPlanDTO alertPlan;

    @Override public String toString()
    {
        return alertPlan.toString();
    }
}