package com.tradehero.th.api.purchase;

import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:24 PM To change this template use File | Settings | File Templates. */
public class UserCreditPlanDTO
{
    public static final String TAG = UserCreditPlanDTO.class.getSimpleName();

    public Date paidUpToUtc;
    public boolean autoRenewingActive;
    public CreditPlanDTO plan;
}
