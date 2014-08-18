package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface IABInventoryFetcher<
        IABSKUType extends IABSKU,
        IABProductDetailsType extends IABProductDetail<IABSKUType>,
        IABExceptionType extends IABException>
    extends BillingInventoryFetcher<
        IABSKUType,
        IABProductDetailsType,
        IABExceptionType>
{
}
