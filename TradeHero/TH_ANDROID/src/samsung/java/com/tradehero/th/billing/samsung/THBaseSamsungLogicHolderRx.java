package com.tradehero.th.billing.samsung;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.th.R;
import com.tradehero.th.billing.THBaseBillingLogicHolderRx;
import com.tradehero.th.billing.samsung.identifier.THSamsungProductIdentifierFetcherHolderRx;
import com.tradehero.th.billing.samsung.inventory.THSamsungInventoryFetcherHolderRx;
import com.tradehero.th.billing.samsung.persistence.THSamsungPurchaseCacheRx;
import com.tradehero.th.billing.samsung.purchase.THSamsungPurchaserHolderRx;
import com.tradehero.th.billing.samsung.purchasefetch.THSamsungPurchaseFetcherHolderRx;
import com.tradehero.th.billing.samsung.report.THSamsungPurchaseReporterHolderRx;
import com.tradehero.th.billing.samsung.tester.THSamsungBillingAvailableTesterHolderRx;
import com.tradehero.th.persistence.billing.samsung.SamsungSKUListCacheRx;
import com.tradehero.th.persistence.billing.samsung.THSamsungProductDetailCacheRx;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

public class THBaseSamsungLogicHolderRx
        extends THBaseBillingLogicHolderRx<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductDetail,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase>
        implements THSamsungLogicHolderRx
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungLogicHolderRx(
            @NonNull SamsungSKUListCacheRx samsungSKUListCache,
            @NonNull THSamsungProductDetailCacheRx thskuDetailCache,
            @NonNull THSamsungPurchaseCacheRx purchaseCache,
            @NonNull THSamsungBillingAvailableTesterHolderRx thSamsungBillingAvailableTesterHolder,
            @NonNull THSamsungProductIdentifierFetcherHolderRx thSamsungProductIdentifierFetcherHolder,
            @NonNull THSamsungInventoryFetcherHolderRx thSamsungInventoryFetcherHolder,
            @NonNull THSamsungPurchaseFetcherHolderRx thSamsungPurchaseFetcherHolder,
            @NonNull THSamsungPurchaserHolderRx thSamsungPurchaserHolder,
            @NonNull THSamsungPurchaseReporterHolderRx thSamsungPurchaseReporterHolder)
    {
        super(
                samsungSKUListCache,
                thskuDetailCache,
                purchaseCache,
                thSamsungBillingAvailableTesterHolder,
                thSamsungProductIdentifierFetcherHolder,
                thSamsungInventoryFetcherHolder,
                thSamsungPurchaseFetcherHolder,
                thSamsungPurchaserHolder,
                thSamsungPurchaseReporterHolder);
    }
    //</editor-fold>

    @Override public String getBillingHolderName(Resources resources)
    {
        return resources.getString(R.string.th_samsung_logic_holder_name);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }

    @NonNull @Override public Observable<ProductInventoryResult<SamsungSKU, THSamsungProductDetail>> getInventory(final int requestCode)
    {
        return getInventory(requestCode, getSkuForInventoryGroup());
    }

    /**
     * This is a HACK because ids and inventory return the same in Samsung.
     * @return
     */
    @NonNull protected List<SamsungSKU> getSkuForInventoryGroup()
    {
        return Collections.singletonList(
                new SamsungSKU(
                        THSamsungConstants.IAP_ITEM_GROUP_ID,
                        THSamsungConstants.EXTRA_CASH_T0_DATA_1));
    }
}
