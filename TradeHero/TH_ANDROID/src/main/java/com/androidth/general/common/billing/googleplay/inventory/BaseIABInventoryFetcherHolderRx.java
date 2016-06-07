package com.androidth.general.common.billing.googleplay.inventory;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABProductDetail;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.inventory.BaseBillingInventoryFetcherHolderRx;
import java.util.List;

abstract public class BaseIABInventoryFetcherHolderRx<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>>
        extends BaseBillingInventoryFetcherHolderRx<
        IABSKUType,
        IABProductDetailType>
        implements IABInventoryFetcherHolderRx<
        IABSKUType,
        IABProductDetailType>
{
    //<editor-fold desc="Constructors">
    public BaseIABInventoryFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected IABInventoryFetcherRx<IABSKUType, IABProductDetailType> createFetcher(
            int requestCode,
            @NonNull List<IABSKUType> productIdentifiers);
}
