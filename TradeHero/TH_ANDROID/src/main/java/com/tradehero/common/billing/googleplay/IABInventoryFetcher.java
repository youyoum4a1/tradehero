package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 3/27/14.
 */
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
