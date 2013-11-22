package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingActor;
import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActor<
                        IABSKUType extends IABSKU,
                        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
                        IABOrderIdType extends IABOrderId,
                        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
                        IABPurchaseHandlerType extends IABPurchaseHandler<
                                                            IABSKUType,
                                                            IABOrderIdType,
                                                            IABPurchaseType,
                                                            IABExceptionType>,
                        IABPurchaseConsumeHandlerType extends IABPurchaseConsumeHandler<
                                                            IABSKUType,
                                                            IABOrderIdType,
                                                            IABPurchaseType,
                                                            IABExceptionType>,
                        IABExceptionType extends IABException>
    extends BillingActor<
                        IABSKUType,
                        IABPurchaseOrderType,
                        IABOrderIdType,
                        IABPurchaseType,
                        IABPurchaseHandlerType,
                        IABExceptionType>
{
    int registerPurchaseConsumeHandler(IABPurchaseConsumeHandlerType purchaseConsumeHandler);
    void launchConsumeSequence(int requestCode, IABPurchaseType purchase);
}
