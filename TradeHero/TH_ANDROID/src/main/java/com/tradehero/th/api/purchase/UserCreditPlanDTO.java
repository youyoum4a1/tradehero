package com.ayondo.academy.api.purchase;

import java.util.Date;

public class UserCreditPlanDTO
{
    public Date paidUpToUtc;
    public boolean autoRenewingActive;
    public CreditPlanDTO plan;
}
