package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingActor;
import com.tradehero.common.billing.BillingActorUser;
import com.tradehero.common.billing.BillingPurchaseHandler;
import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActorUser<ProductDetailsType extends ProductDetails<SKU>,
                        IABExceptionType extends IABException,
                        IABPurchaseHandlerType extends IABPurchaseHandler<IABExceptionType>,
                        IABActorType extends BillingActor<SKU, ProductDetailsType, IABExceptionType, IABOrderId, IABPurchase, IABPurchaseHandlerType>>
    extends BillingActorUser<SKU, ProductDetailsType, IABExceptionType, IABOrderId, IABPurchase, IABPurchaseHandlerType, IABActorType>
{
}
