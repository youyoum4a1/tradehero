package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingActor;
import com.tradehero.common.billing.BillingActorUser;
import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActorUser<
                        IABSKUType extends IABSKU,
                        ProductDetailsType extends ProductDetails<IABSKUType>,
                        IABOrderIdType extends IABOrderId,
                        IABPurchaseType extends IABPurchase<IABOrderIdType, IABSKUType>,
                        IABPurchaseHandlerType extends IABPurchaseHandler<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType>,
                        IABActorType extends BillingActor<
                                                    IABSKUType,
                                                    ProductDetailsType,
                                                    IABOrderIdType,
                                                    IABPurchaseType,
                                                    IABPurchaseHandlerType,
                                                    IABExceptionType>,
                        IABExceptionType extends IABException>
    extends BillingActorUser<
                        IABSKUType,
                        ProductDetailsType,
                        IABOrderIdType,
                        IABPurchaseType,
                        IABPurchaseHandlerType,
                        IABActorType,
                        IABExceptionType>
{
}
