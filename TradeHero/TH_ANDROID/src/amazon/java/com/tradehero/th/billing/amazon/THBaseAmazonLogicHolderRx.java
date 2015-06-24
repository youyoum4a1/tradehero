package com.tradehero.th.billing.amazon;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.consume.PurchaseConsumedResult;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBaseBillingLogicHolderRx;
import com.tradehero.th.billing.THProductDetailDomainPredicate;
import com.tradehero.th.billing.amazon.consume.THAmazonPurchaseConsumerHolderRx;
import com.tradehero.th.billing.amazon.identifier.THAmazonProductIdentifierFetcherHolderRx;
import com.tradehero.th.billing.amazon.identifier.THBaseAmazonProductIdentifierFetcherRx;
import com.tradehero.th.billing.amazon.inventory.THAmazonInventoryFetcherHolderRx;
import com.tradehero.th.billing.amazon.purchase.THAmazonPurchaserHolderRx;
import com.tradehero.th.billing.amazon.purchasefetch.THAmazonPurchaseFetcherHolderRx;
import com.tradehero.th.billing.amazon.report.THAmazonPurchaseReporterHolderRx;
import com.tradehero.th.billing.amazon.tester.THAmazonBillingAvailableTesterHolderRx;
import com.tradehero.th.billing.report.PurchaseReportResult;
import com.tradehero.th.persistence.billing.AmazonSKUListCacheRx;
import com.tradehero.th.persistence.billing.THAmazonProductDetailCacheRx;
import com.tradehero.th.persistence.billing.THAmazonPurchaseCacheRx;
import com.tradehero.th.rx.ReplaceWith;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class THBaseAmazonLogicHolderRx
    extends THBaseBillingLogicHolderRx<
            AmazonSKUListKey,
            AmazonSKU,
            AmazonSKUList,
            THAmazonProductDetail,
            THAmazonPurchaseOrder,
            THAmazonOrderId,
            THAmazonPurchase>
    implements THAmazonLogicHolderRx
{
    @NonNull private final THAmazonPurchaseConsumerHolderRx purchaseConsumerHolder;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonLogicHolderRx(
            @NonNull AmazonSKUListCacheRx amazonSKUListCache,
            @NonNull THAmazonProductDetailCacheRx thskuDetailCache,
            @NonNull THAmazonPurchaseCacheRx  purchaseCacheRx,
            @NonNull THAmazonBillingAvailableTesterHolderRx thAmazonBillingAvailableTesterHolder,
            @NonNull THAmazonProductIdentifierFetcherHolderRx thAmazonProductIdentifierFetcherHolder,
            @NonNull THAmazonInventoryFetcherHolderRx thAmazonInventoryFetcherHolder,
            @NonNull THAmazonPurchaseFetcherHolderRx thAmazonPurchaseFetcherHolder,
            @NonNull THAmazonPurchaserHolderRx thAmazonPurchaserHolder,
            @NonNull THAmazonPurchaseReporterHolderRx thAmazonPurchaseReporterHolder,
            @NonNull THAmazonPurchaseConsumerHolderRx purchaseConsumerHolder)
    {
        super(
                amazonSKUListCache,
                thskuDetailCache,
                purchaseCacheRx,
                thAmazonBillingAvailableTesterHolder,
                thAmazonProductIdentifierFetcherHolder,
                thAmazonInventoryFetcherHolder,
                thAmazonPurchaseFetcherHolder,
                thAmazonPurchaserHolder,
                thAmazonPurchaseReporterHolder);
        this.purchaseConsumerHolder = purchaseConsumerHolder;
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
        return resources.getString(R.string.th_amazon_logic_holder_name);
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

    @Override public List<THAmazonProductDetail> getDetailsOfDomain(ProductIdentifierDomain domain)
    {
        List<THAmazonProductDetail> details = productDetailCache.getValues(THBaseAmazonProductIdentifierFetcherRx.getAllSkus());
        if (details == null)
        {
            return null;
        }
        return CollectionUtils.filter(
                details,
                new THProductDetailDomainPredicate<AmazonSKU, THAmazonProductDetail>(domain));
    }

    @NonNull @Override public Observable<PurchaseReportResult<
            AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase>> report(
            final int requestCode,
            @NonNull THAmazonPurchase purchase, @NonNull THAmazonProductDetail productDetail)
    {
        return super.report(requestCode, purchase, productDetail)
                .flatMap(
                        new Func1<PurchaseReportResult<AmazonSKU, THAmazonOrderId, THAmazonPurchase>, Observable<? extends PurchaseReportResult<AmazonSKU, THAmazonOrderId, THAmazonPurchase>>>()
                        {
                            @Override public Observable<? extends PurchaseReportResult<AmazonSKU, THAmazonOrderId, THAmazonPurchase>> call(
                                    PurchaseReportResult<AmazonSKU, THAmazonOrderId, THAmazonPurchase> result)
                            {
                                return THBaseAmazonLogicHolderRx.this.consume(requestCode, result.reportedPurchase)
                                        .map(new ReplaceWith<>(result));
                            }
                        });
    }

    @NonNull @Override public Observable<PurchaseConsumedResult<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase>> consume(
            int requestCode,
            @NonNull THAmazonPurchase purchase)
    {
        return purchaseConsumerHolder.get(requestCode, purchase);
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
