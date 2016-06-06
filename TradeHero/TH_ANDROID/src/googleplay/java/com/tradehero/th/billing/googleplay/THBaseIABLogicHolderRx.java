package com.androidth.general.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUList;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.billing.googleplay.consume.PurchaseConsumeResult;
import com.tradehero.th.R;
import com.androidth.general.billing.THBaseBillingLogicHolderRx;
import com.androidth.general.billing.googleplay.consumer.THIABPurchaseConsumerHolderRx;
import com.androidth.general.billing.googleplay.identifier.THIABProductIdentifierFetcherHolderRx;
import com.androidth.general.billing.googleplay.inventory.THIABInventoryFetcherHolderRx;
import com.androidth.general.billing.googleplay.purchase.THIABPurchaserHolderRx;
import com.androidth.general.billing.googleplay.purchasefetch.THIABPurchaseFetcherHolderRx;
import com.androidth.general.billing.googleplay.report.THIABPurchaseReporterHolderRx;
import com.androidth.general.billing.googleplay.tester.THIABBillingAvailableTesterHolderRx;
import com.androidth.general.billing.report.PurchaseReportResult;
import com.androidth.general.persistence.billing.googleplay.IABSKUListCacheRx;
import com.androidth.general.persistence.billing.googleplay.THIABProductDetailCacheRx;
import com.androidth.general.persistence.billing.googleplay.THIABPurchaseCacheRx;
import com.androidth.general.rx.ReplaceWithFunc1;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class THBaseIABLogicHolderRx
        extends THBaseBillingLogicHolderRx<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THIABProductDetail,
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
    @NonNull @Override public Observable<PurchaseReportResult<IABSKU, THIABOrderId, THIABPurchase>> report(final int requestCode,
            @NonNull THIABPurchase purchase, @NonNull THIABProductDetail productDetail)
    {
        return super.report(requestCode, purchase, productDetail)
                .flatMap(
                        new Func1<PurchaseReportResult<IABSKU, THIABOrderId, THIABPurchase>, Observable<? extends PurchaseReportResult<IABSKU, THIABOrderId, THIABPurchase>>>()
                        {
                            @Override public Observable<? extends PurchaseReportResult<IABSKU, THIABOrderId, THIABPurchase>> call(
                                    PurchaseReportResult<IABSKU, THIABOrderId, THIABPurchase> reportResult)
                            {
                                return THBaseIABLogicHolderRx.this.consume(requestCode, reportResult.reportedPurchase)
                                        .map(new ReplaceWithFunc1<>(reportResult));
                            }
                        });
    }
    //</editor-fold>

    //<editor-fold desc="Consume Purchase">
    @NonNull @Override public Observable<PurchaseConsumeResult<
            IABSKU,
            THIABOrderId,
            THIABPurchase>> consumeAndClear(
            final int requestCode,
            @NonNull THIABPurchase purchase)
    {
        return consume(requestCode, purchase)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        THBaseIABLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
    }

    @NonNull @Override public Observable<PurchaseConsumeResult<
            IABSKU,
            THIABOrderId,
            THIABPurchase>> consume(
            int requestCode,
            @NonNull THIABPurchase purchase)
    {
        return purchaseConsumerHolder.get(requestCode, purchase);
    }
    //</editor-fold>

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(activity, requestCode, resultCode, data);
        purchaseConsumerHolder.onActivityResult(activity, requestCode, resultCode, data);
    }
}
