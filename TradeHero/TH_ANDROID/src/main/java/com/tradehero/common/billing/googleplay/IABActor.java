package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingActor;
import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActor<
                        IABSKUType extends IABSKU,
                        ProductDetailsType extends ProductDetails<IABSKUType>,
                        IABOrderIdType extends IABOrderId,
                        IABExceptionType extends IABException,
                        IABPurchaseType extends IABPurchase<IABOrderIdType, IABSKUType>,
                        IABPurchaseHandlerType extends IABPurchaseHandler<IABSKUType, IABOrderIdType, IABExceptionType, IABPurchaseType>>
    extends BillingActor<
                        IABSKUType,
                        ProductDetailsType,
                        IABExceptionType,
                        IABOrderIdType,
                        IABPurchaseType,
                        IABPurchaseHandlerType>
{
    int launchPurchaseSequence(IABPurchaseHandlerType billingPurchaseHandler, ProductDetailsType productDetails, String extraData);
}
