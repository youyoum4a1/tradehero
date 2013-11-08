package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseHandler;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:16 PM To change this template use File | Settings | File Templates. */
public interface IABPurchaseHandler<
                                IABExceptionType extends IABException,
                                IABPurchaseType extends IABPurchase<IABOrderId, IABSKU>>
    extends BillingPurchaseHandler<
                                IABOrderId,
                                IABSKU,
                                IABPurchaseType,
                                IABExceptionType>
{
}
