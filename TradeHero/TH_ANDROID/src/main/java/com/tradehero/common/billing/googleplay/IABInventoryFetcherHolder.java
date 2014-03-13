package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABInventoryFetcherHolder<
        IABSKUType extends IABSKU,
        IABProductDetailsType extends IABProductDetail<IABSKUType>,
        IABExceptionType extends IABException>
    extends BillingInventoryFetcherHolder<
                IABSKUType,
                IABProductDetailsType,
                IABExceptionType>
{
}
