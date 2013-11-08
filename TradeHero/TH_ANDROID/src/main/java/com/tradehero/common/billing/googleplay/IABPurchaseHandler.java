package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.ProductIdentifier;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:16 PM To change this template use File | Settings | File Templates. */
public interface IABPurchaseHandler<ProductIdentifierType extends ProductIdentifier,
                                    ProducDetailsType extends ProductDetails<ProductIdentifierType>,
                                    ExceptionType extends Exception>
{
    void handleException(int requestCode, ExceptionType exception);
}
