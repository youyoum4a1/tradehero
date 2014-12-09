package com.tradehero.th.billing;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseBillingLogicHolderRx;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.ProductDetailCacheRx;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListCacheRx;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.ProductPurchaseCacheRx;
import com.tradehero.common.billing.identifier.ProductIdentifierFetcherHolderRx;
import com.tradehero.common.billing.inventory.BillingInventoryFetcherHolderRx;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.billing.purchase.BillingPurchaserHolderRx;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.billing.purchasefetch.BillingPurchaseFetcherHolderRx;
import com.tradehero.common.billing.restore.PurchaseRestoreResult;
import com.tradehero.common.billing.tester.BillingAvailableTesterHolderRx;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.billing.report.PurchaseReportResult;
import com.tradehero.th.billing.report.THPurchaseReporterHolderRx;
import java.util.Collections;
import rx.Observable;

abstract public class THBaseBillingLogicHolderRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THProductTunerType extends THProductDetailTuner<ProductIdentifierType, THProductDetailType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends
        BaseBillingLogicHolderRx<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THProductTunerType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType>
        implements
        THBillingLogicHolderRx<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType>
{
    @NonNull protected final THPurchaseReporterHolderRx<
            ProductIdentifierType,
            THProductDetailType,
            THOrderIdType,
            THProductPurchaseType>
            purchaseReporterHolder;

    //<editor-fold desc="Constructors">
    public THBaseBillingLogicHolderRx(
            @NonNull
            ProductIdentifierListCacheRx<ProductIdentifierType, ProductIdentifierListKeyType, ProductIdentifierListType> productIdentifierCache,
            @NonNull ProductDetailCacheRx<ProductIdentifierType, THProductDetailType, THProductTunerType> productDetailCache,
            @NonNull ProductPurchaseCacheRx<ProductIdentifierType, THOrderIdType, THProductPurchaseType> purchaseCache,
            @NonNull BillingAvailableTesterHolderRx billingAvailableTesterHolder,
            @NonNull
            ProductIdentifierFetcherHolderRx<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType> productIdentifierFetcherHolder,
            @NonNull BillingInventoryFetcherHolderRx<ProductIdentifierType, THProductDetailType> inventoryFetcherHolder,
            @NonNull
            BillingPurchaseFetcherHolderRx<ProductIdentifierType, THOrderIdType, THProductPurchaseType> purchaseFetcherHolder,
            @NonNull
            BillingPurchaserHolderRx<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType> purchaserHolder,
            @NonNull
            THPurchaseReporterHolderRx<ProductIdentifierType, THProductDetailType, THOrderIdType, THProductPurchaseType> purchaseReporterHolder)
    {
        super(
                productIdentifierCache,
                productDetailCache,
                purchaseCache,
                billingAvailableTesterHolder,
                productIdentifierFetcherHolder,
                inventoryFetcherHolder,
                purchaseFetcherHolder,
                purchaserHolder);
        this.purchaseReporterHolder = purchaseReporterHolder;
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    @Override public void onDestroy()
    {
        purchaseReporterHolder.onDestroy();
        super.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="Request Code Management">
    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                purchaseReporterHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        purchaseReporterHolder.forgetRequestCode(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Purchase">
    @NonNull @Override public Observable<PurchaseResult<
            ProductIdentifierType,
            THPurchaseOrderType,
            THOrderIdType,
            THProductPurchaseType>> purchase(
            int requestCode,
            @NonNull THPurchaseOrderType purchaseOrder)
    {
        return super.purchase(requestCode, purchaseOrder)
                .flatMap(purchaseResult -> report(
                        purchaseResult.requestCode,
                        purchaseResult.purchase)
                        .map(reportResult -> purchaseResult));
    }
    //</editor-fold>

    //<editor-fold desc="Report Purchase">
    @NonNull @Override public Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> reportAndClear(int requestCode,
            @NonNull THProductPurchaseType purchase)
    {
        return report(requestCode, purchase)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> report(int requestCode,
            @NonNull THProductPurchaseType purchase)
    {
        return getInventory(requestCode, Collections.singletonList(purchase.getProductIdentifier()))
                .flatMap(result -> report(requestCode, purchase, result.detail));
    }

    @NonNull @Override public Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> reportAndClear(int requestCode,
            @NonNull THProductPurchaseType purchase, @NonNull THProductDetailType productDetail)
    {
        return report(requestCode, purchase, productDetail)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> report(int requestCode,
            @NonNull THProductPurchaseType purchase, @NonNull THProductDetailType productDetail)
    {
        THToast.show("Reporting " + purchase.getProductIdentifier());
        return purchaseReporterHolder.get(requestCode, purchase, productDetail)
                .doOnNext(result -> THToast.show("Reported " + result.reportedPurchase.getProductIdentifier()));
    }
    //</editor-fold>

    //<editor-fold desc="Restore Purchase">
    @NonNull @Override public Observable<PurchaseRestoreResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> restorePurchase(
            int requestCode,
            @NonNull THProductPurchaseType purchase)
    {
        return super.restorePurchase(requestCode, purchase)
                .flatMap(restored -> report(requestCode, restored.purchase)
                        .map(reported -> restored));
    }
    //</editor-fold>

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            THProductDetailType>> getDetailsOfDomain(
            int requestCode,
            @NonNull ProductIdentifierDomain domain)
    {
        return getInventory(requestCode)
                .filter(result -> result.detail.getDomain().equals(domain));
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        purchaseReporterHolder.onActivityResult(requestCode, resultCode, data);
    }
}
