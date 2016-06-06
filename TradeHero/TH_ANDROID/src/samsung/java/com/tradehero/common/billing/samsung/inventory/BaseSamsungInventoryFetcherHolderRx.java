package com.androidth.general.common.billing.samsung.inventory;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.inventory.BaseBillingInventoryFetcherHolderRx;
import com.androidth.general.common.billing.samsung.SamsungProductDetail;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import java.util.List;

abstract public class BaseSamsungInventoryFetcherHolderRx<
        SamsungSKUType extends SamsungSKU,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>>
        extends BaseBillingInventoryFetcherHolderRx<
        SamsungSKUType,
        SamsungProductDetailType>
        implements SamsungInventoryFetcherHolderRx<
        SamsungSKUType,
        SamsungProductDetailType>
{
    //<editor-fold desc="Constructors">
    public BaseSamsungInventoryFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected SamsungInventoryFetcherRx<SamsungSKUType, SamsungProductDetailType> createFetcher(int requestCode,
            @NonNull List<SamsungSKUType> productIdentifiers);

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
