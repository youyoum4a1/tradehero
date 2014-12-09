package com.tradehero.common.billing.googleplay.inventory;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABProductDetail;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.inventory.BaseBillingInventoryFetcherHolderRx;
import com.tradehero.common.billing.inventory.BillingInventoryFetcherRx;
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

    @Override public void onDestroy()
    {
        for (BillingInventoryFetcherRx<IABSKUType, IABProductDetailType> actor : actors.values())
        {
            ((IABInventoryFetcherRx<IABSKUType, IABProductDetailType>) actor).onDestroy();
        }
        super.onDestroy();
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        IABInventoryFetcherRx<IABSKUType, IABProductDetailType> actor = (IABInventoryFetcherRx<IABSKUType, IABProductDetailType>) actors.get(requestCode);
        if (actor != null)
        {
            actor.onDestroy();
        }
        super.forgetRequestCode(requestCode);
    }
}
