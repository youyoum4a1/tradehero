package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingActor;
import com.tradehero.common.billing.BillingActorUser;
import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActorUser<
                        ProductDetailsType extends ProductDetails<IABSKU>,
                        IABExceptionType extends IABException,
                        IABPurchaseType extends IABPurchase<IABOrderId, IABSKU>,
                        IABPurchaseHandlerType extends IABPurchaseHandler<IABExceptionType, IABPurchaseType>,
                        IABActorType extends BillingActor<
                                                    IABSKU,
                                                    ProductDetailsType,
                                                    IABExceptionType,
                                                    IABOrderId,
                                                    IABPurchaseType,
                                                    IABPurchaseHandlerType>>
    extends BillingActorUser<
                        IABSKU,
                        ProductDetailsType,
                        IABExceptionType,
                        IABOrderId,
                        IABPurchaseType,
                        IABPurchaseHandlerType,
                        IABActorType>
{
}
