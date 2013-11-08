package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.googleplay.IABActor;
import com.tradehero.common.billing.googleplay.SKU;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActorUser<ProductIdentifierType extends SKU,
                        ProductDetailsType extends ProductDetails<ProductIdentifierType>>
{
    void setBillingActor(IABActor<ProductIdentifierType, ProductDetailsType> billingActor);
    IABActor<ProductIdentifierType, ProductDetailsType> getBillingActor();
}
