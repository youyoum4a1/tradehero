package com.tradehero.th.billing.googleplay;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.consume.PurchaseConsumeResult;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.billing.THBaseBillingLogicHolderRx;
import com.tradehero.th.billing.googleplay.consumer.THIABPurchaseConsumerHolderRx;
import com.tradehero.th.billing.googleplay.identifier.THIABProductIdentifierFetcherHolderRx;
import com.tradehero.th.billing.googleplay.inventory.THIABInventoryFetcherHolderRx;
import com.tradehero.th.billing.googleplay.purchase.THIABPurchaserHolderRx;
import com.tradehero.th.billing.googleplay.purchasefetch.THIABPurchaseFetcherHolderRx;
import com.tradehero.th.billing.googleplay.report.THIABPurchaseReporterHolderRx;
import com.tradehero.th.billing.googleplay.tester.THIABBillingAvailableTesterHolderRx;
import com.tradehero.th.billing.report.PurchaseReportResult;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCacheRx;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCacheRx;
import com.tradehero.th.persistence.billing.googleplay.THIABPurchaseCacheRx;
import javax.inject.Inject;
import rx.Observable;

public class THBaseIABLogicHolderRx
        extends THBaseBillingLogicHolderRx<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THIABProductDetail,
        THIABProductDetailTuner,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase>
        implements THIABLogicHolderRx
{
    @NonNull private final THIABPurchaseConsumerHolderRx purchaseConsumerHolder;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABLogicHolderRx(
            @NonNull IABSKUListCacheRx iabskuListCache,
            @NonNull THIABProductDetailCacheRx thskuDetailCache,
            @NonNull THIABPurchaseCacheRx thPurchaseCacheRx,
            @NonNull THIABBillingAvailableTesterHolderRx thiabBillingAvailableTesterHolder,
            @NonNull THIABProductIdentifierFetcherHolderRx thBaseIABProductIdentifierFetcherHolder,
            @NonNull THIABInventoryFetcherHolderRx thiabInventoryFetcherHolder,
            @NonNull THIABPurchaseFetcherHolderRx thiabPurchaseFetcherHolder,
            @NonNull THIABPurchaserHolderRx thiabPurchaserHolder,
            @NonNull THIABPurchaseReporterHolderRx thiabPurchaseReporterHolder,
            @NonNull THIABPurchaseConsumerHolderRx thiabPurchaseConsumerHolder)
    {
        super(
                iabskuListCache,
                thskuDetailCache,
                thPurchaseCacheRx,
                thiabBillingAvailableTesterHolder,
                thBaseIABProductIdentifierFetcherHolder,
                thiabInventoryFetcherHolder,
                thiabPurchaseFetcherHolder,
                thiabPurchaserHolder,
                thiabPurchaseReporterHolder);
        this.purchaseConsumerHolder = thiabPurchaseConsumerHolder;
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    @Override public void onDestroy()
    {
        purchaseConsumerHolder.onDestroy();
        super.onDestroy();
    }
    //</editor-fold>

    @Override public String getBillingHolderName(Resources resources)
    {
        return resources.getString(R.string.th_iab_logic_holder_name);
    }

    //<editor-fold desc="Request Code Management">
    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode)
                && purchaseConsumerHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        purchaseConsumerHolder.forgetRequestCode(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Report Purchase">
    @NonNull @Override public Observable<PurchaseReportResult<IABSKU, THIABOrderId, THIABPurchase>> report(int requestCode,
            @NonNull THIABPurchase purchase, @NonNull THIABProductDetail productDetail)
    {
        return super.report(requestCode, purchase, productDetail)
                .flatMap(reportResult -> consume(requestCode, reportResult.reportedPurchase)
                        .map(consumeResult -> reportResult));
    }
    //</editor-fold>

    //<editor-fold desc="Consume Purchase">
    @NonNull @Override public Observable<PurchaseConsumeResult<
            IABSKU,
            THIABOrderId,
            THIABPurchase>> consumeAndClear(
            int requestCode,
            @NonNull THIABPurchase purchase)
    {
        return consume(requestCode, purchase)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<PurchaseConsumeResult<
            IABSKU,
            THIABOrderId,
            THIABPurchase>> consume(
            int requestCode,
            @NonNull THIABPurchase purchase)
    {
        THToast.show("Consuming " + purchase.getProductIdentifier());
        return purchaseConsumerHolder.get(requestCode, purchase)
                .doOnNext(result -> THToast.show("Consumed " + purchase.getProductIdentifier()));
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        purchaseConsumerHolder.onActivityResult(requestCode, resultCode, data);
    }
}
