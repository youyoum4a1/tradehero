package com.tradehero.th.api.alert;

import java.util.Date;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:10 PM Copyright (c) TradeHero */
public class UserAlertPlanDTO
{
    public Date paidUpToUtc;
    public boolean autoRenewingActive;
    public AlertPlanDTO alertPlan;
}