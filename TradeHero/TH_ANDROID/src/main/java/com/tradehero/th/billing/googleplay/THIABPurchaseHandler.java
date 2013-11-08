package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseHandler;
import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.googleplay.SKU;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:16 PM To change this template use File | Settings | File Templates. */
public interface THIABPurchaseHandler<ProductIdentifierType extends SKU,
                                    ProducDetailsType extends ProductDetails<ProductIdentifierType>,
                                    ExceptionType extends IABException>
    extends BillingPurchaseHandler<ProductIdentifierType, ProducDetailsType, ExceptionType>
{
}
