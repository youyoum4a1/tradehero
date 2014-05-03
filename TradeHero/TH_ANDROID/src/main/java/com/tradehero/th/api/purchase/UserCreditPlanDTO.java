package com.tradehero.th.api.purchase;

import java.util.Date;


public class UserCreditPlanDTO
{
    public static final String TAG = UserCreditPlanDTO.class.getSimpleName();

    public Date paidUpToUtc;
    public boolean autoRenewingActive;
    public CreditPlanDTO plan;
}
