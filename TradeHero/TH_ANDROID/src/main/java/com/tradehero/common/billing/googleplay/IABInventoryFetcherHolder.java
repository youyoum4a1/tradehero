package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;

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
