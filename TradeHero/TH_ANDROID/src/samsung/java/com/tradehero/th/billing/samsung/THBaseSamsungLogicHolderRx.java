package com.tradehero.th.billing.samsung;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.billing.restore.PurchaseRestoreTotalResult;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

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

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }

    @NonNull @Override public Observable<ProductInventoryResult<SamsungSKU, THSamsungProductDetail>> getInventory(final int requestCode)
    {
        return getInventory(requestCode, getSkuForInventoryGroup());
    }

    @NonNull @Override public Observable<ProductInventoryResult<SamsungSKU, THSamsungProductDetail>> getInventory(final int requestCode,
            @NonNull List<SamsungSKU> productIdentifiers)
    {
        final Map<SamsungSKU, THSamsungProductDetail> details = productDetailCache.getMap(productIdentifiers);
        List<SamsungSKU> missing = new ArrayList<>();
        for (SamsungSKU candidate : productIdentifiers)
        {
            if (details.get(candidate) == null)
            {
                details.remove(candidate);
                missing.add(candidate);
            }
        }
        if (missing.size() == 0)
        {
            return Observable.just(new ProductInventoryResult<>(requestCode, details));
        }
        return super.getInventory(requestCode, missing)
                .map(new Func1<ProductInventoryResult<SamsungSKU, THSamsungProductDetail>, ProductInventoryResult<SamsungSKU, THSamsungProductDetail>>()
                {
                    @Override public ProductInventoryResult<SamsungSKU, THSamsungProductDetail> call(
                            ProductInventoryResult<SamsungSKU, THSamsungProductDetail> detailResult)
                    {
                        for (THSamsungProductDetail detail : detailResult.mapped.values())
                        {
                            details.put(detail.getProductIdentifier(), detail);
                        }
                        return new ProductInventoryResult<>(requestCode, details);
                    }
                });
    }

    @NonNull @Override public Observable<PurchaseRestoreTotalResult<SamsungSKU, THSamsungOrderId, THSamsungPurchase>> restorePurchases(
            final int requestCode)
    {
        return getInventory(requestCode)
                .flatMap(
                        new Func1<ProductInventoryResult<SamsungSKU, THSamsungProductDetail>, Observable<PurchaseRestoreTotalResult<SamsungSKU, THSamsungOrderId, THSamsungPurchase>>>()
                        {
                            @Override public Observable<PurchaseRestoreTotalResult<SamsungSKU, THSamsungOrderId, THSamsungPurchase>> call(
                                    ProductInventoryResult<SamsungSKU, THSamsungProductDetail> ignored)
                            {
                                return THBaseSamsungLogicHolderRx.super.restorePurchases(requestCode);
                            }
                        });
    }

    /**
     * This is a HACK because ids and inventory return the same in Samsung.
     */
    @NonNull protected List<SamsungSKU> getSkuForInventoryGroup()
    {
        return Collections.singletonList(
                new SamsungSKU(
                        "Fake"));
    }
}
