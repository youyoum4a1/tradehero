package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingActor;
import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.googleplay.SKU;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActor<ProductIdentifierType extends SKU,
                        ProductDetailsType extends ProductDetails<ProductIdentifierType>>
    extends BillingActor<ProductIdentifierType, ProductDetailsType>
{
}
