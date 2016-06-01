package com.ayondo.academy.api.alert;

import java.util.Date;

public class UserAlertPlanDTO
{
    public Date paidUpToUtc;
    public boolean autoRenewingActive;
    public AlertPlanDTO alertPlan;
}