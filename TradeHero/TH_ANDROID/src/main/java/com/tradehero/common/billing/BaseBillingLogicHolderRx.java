package com.tradehero.common.billing;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.identifier.ProductIdentifierFetcherHolderRx;
import com.tradehero.common.billing.identifier.ProductIdentifierListResult;
import com.tradehero.common.billing.inventory.BillingInventoryFetcherHolderRx;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.billing.purchase.BillingPurchaserHolderRx;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.billing.purchasefetch.BillingPurchaseFetcherHolderRx;
import com.tradehero.common.billing.purchasefetch.PurchaseFetchResult;
import com.tradehero.common.billing.restore.PurchaseRestoreResult;
import com.tradehero.common.billing.restore.PurchaseRestoreResultWithError;
import com.tradehero.common.billing.restore.PurchaseRestoreTotalResult;
import com.tradehero.common.billing.tester.BillingAvailableTesterHolderRx;
import com.tradehero.common.billing.tester.BillingTestResult;
import com.tradehero.common.utils.THToast;
import java.util.List;
import rx.Observable;

abstract public class BaseBillingLogicHolderRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        ProductTunerType extends ProductDetailTuner<ProductIdentifierType, ProductDetailType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        implements BillingLogicHolderRx<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType>
{
    public static final int MAX_RANDOM_RETRIES = 50;

    @NonNull protected final ProductIdentifierListCacheRx<ProductIdentifierType, ProductIdentifierListKeyType, ProductIdentifierListType>
            productIdentifierCache;
    @NonNull protected final ProductDetailCacheRx<ProductIdentifierType, ProductDetailType, ProductTunerType> productDetailCache;
    @NonNull protected final ProductPurchaseCacheRx<ProductIdentifierType, OrderIdType, ProductPurchaseType> purchaseCache;
    @NonNull protected final BillingAvailableTesterHolderRx billingAvailableTesterHolder;
    @NonNull protected final ProductIdentifierFetcherHolderRx<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType>
            productIdentifierFetcherHolder;
    @NonNull protected final BillingInventoryFetcherHolderRx<ProductIdentifierType, ProductDetailType> inventoryFetcherHolder;
    @NonNull protected final BillingPurchaseFetcherHolderRx<ProductIdentifierType, OrderIdType, ProductPurchaseType>
            purchaseFetcherHolder;
    @NonNull protected final BillingPurchaserHolderRx<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType>
            purchaserHolder;

    //<editor-fold desc="Constructors">
    public BaseBillingLogicHolderRx(
            @NonNull
            ProductIdentifierListCacheRx<ProductIdentifierType, ProductIdentifierListKeyType, ProductIdentifierListType> productIdentifierCache,
            @NonNull ProductDetailCacheRx<ProductIdentifierType, ProductDetailType, ProductTunerType> productDetailCache,
            @NonNull ProductPurchaseCacheRx<ProductIdentifierType, OrderIdType, ProductPurchaseType> purchaseCache,
            @NonNull BillingAvailableTesterHolderRx billingAvailableTesterHolder,
            @NonNull
            ProductIdentifierFetcherHolderRx<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType> productIdentifierFetcherHolder,
            @NonNull BillingInventoryFetcherHolderRx<ProductIdentifierType, ProductDetailType> inventoryFetcherHolder,
            @NonNull
            BillingPurchaseFetcherHolderRx<ProductIdentifierType, OrderIdType, ProductPurchaseType> purchaseFetcherHolder,
            @NonNull
            BillingPurchaserHolderRx<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType> purchaserHolder)
    {
        super();
        this.productIdentifierCache = productIdentifierCache;
        this.productDetailCache = productDetailCache;
        this.purchaseCache = purchaseCache;
        this.billingAvailableTesterHolder = billingAvailableTesterHolder;
        this.productIdentifierFetcherHolder = productIdentifierFetcherHolder;
        this.inventoryFetcherHolder = inventoryFetcherHolder;
        this.purchaseFetcherHolder = purchaseFetcherHolder;
        this.purchaserHolder = purchaserHolder;
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    @Override public void onDestroy()
    {
        billingAvailableTesterHolder.onDestroy();
        productIdentifierFetcherHolder.onDestroy();
        inventoryFetcherHolder.onDestroy();
        purchaseFetcherHolder.onDestroy();
        purchaserHolder.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="Request Code Management">
    @Override public int getUnusedRequestCode()
    {
        int retries = MAX_RANDOM_RETRIES;
        int randomNumber;
        while (retries-- > 0)
        {
            randomNumber = (int) (Math.random() * Integer.MAX_VALUE);
            if (isUnusedRequestCode(randomNumber))
            {
                return randomNumber;
            }
        }
        throw new IllegalStateException("Could not find an unused requestCode after " + MAX_RANDOM_RETRIES + " trials");
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return billingAvailableTesterHolder.isUnusedRequestCode(requestCode) &&
                productIdentifierFetcherHolder.isUnusedRequestCode(requestCode) &&
                inventoryFetcherHolder.isUnusedRequestCode(requestCode) &&
                purchaseFetcherHolder.isUnusedRequestCode(requestCode) &&
                purchaserHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        billingAvailableTesterHolder.forgetRequestCode(requestCode);
        productIdentifierFetcherHolder.forgetRequestCode(requestCode);
        inventoryFetcherHolder.forgetRequestCode(requestCode);
        purchaseFetcherHolder.forgetRequestCode(requestCode);
        purchaserHolder.forgetRequestCode(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Test Billing">
    @NonNull @Override public Observable<BillingTestResult> testAndClear(int requestCode)
    {
        return test(requestCode)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<BillingTestResult> test(int requestCode)
    {
        return billingAvailableTesterHolder.get(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Get Product Identifiers">
    @NonNull @Override public Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> getIdsAndClear(
            int requestCode)
    {
        return getIds(requestCode)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> getIds(
            int requestCode)
    {
        return test(requestCode)
                .flatMap(result -> productIdentifierFetcherHolder.get(requestCode))
                .doOnNext(result -> productIdentifierCache.onNext(result.type, result.productIdentifiers));
    }
    //</editor-fold>

    //<editor-fold desc="Get Inventory">
    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventoryAndClear(int requestCode)
    {
        return getInventory(requestCode)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventory(int requestCode)
    {
        return getIds(requestCode)
                .flatMap(result -> getInventory(result.requestCode, result.productIdentifiers));
    }

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventoryAndClear(int requestCode, @NonNull List<ProductIdentifierType> productIdentifiers)
    {
        return getInventory(requestCode, productIdentifiers)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventory(int requestCode, @NonNull List<ProductIdentifierType> productIdentifiers)
    {
        return test(requestCode)
                .flatMap(result -> inventoryFetcherHolder.get(requestCode, productIdentifiers))
                .doOnNext(result -> productDetailCache.onNext(result.id, result.detail));
    }
    //</editor-fold>

    //<editor-fold desc="Purchase">
    @NonNull @Override public Observable<PurchaseResult<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> purchaseAndClear(int requestCode, @NonNull PurchaseOrderType purchaseOrder)
    {
        return purchase(requestCode, purchaseOrder)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<PurchaseResult<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> purchase(int requestCode, @NonNull PurchaseOrderType purchaseOrder)
    {
        return test(requestCode)
                .flatMap(result -> purchaserHolder.get(requestCode, purchaseOrder))
                .doOnNext(result -> purchaseCache.onNext(result.purchase.getOrderId(), result.purchase));
    }
    //</editor-fold>

    //<editor-fold desc="Purchases Fetch">
    @NonNull @Override public Observable<PurchaseFetchResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> getPurchasesAndClear(int requestCode)
    {
        return getPurchases(requestCode)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<PurchaseFetchResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> getPurchases(int requestCode)
    {
        return test(requestCode)
                .flatMap(result -> purchaseFetcherHolder.get(requestCode))
                .doOnNext(result -> purchaseCache.onNext(result.purchase.getOrderId(), result.purchase))
                .doOnNext(result -> THToast.show("Saved purchase in cache " + result.purchase.getProductIdentifier()));
    }
    //</editor-fold>

    //<editor-fold desc="Restore Purchases">
    @NonNull @Override public Observable<PurchaseRestoreResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> restorePurchaseAndClear(
            int requestCode,
            @NonNull ProductPurchaseType purchase)
    {
        return restorePurchase(requestCode, purchase)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<PurchaseRestoreResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> restorePurchase(
            int requestCode,
            @NonNull ProductPurchaseType purchase)
    {
        // This default one does not have much to do actually
        return Observable.just(new PurchaseRestoreResult<>(requestCode, purchase));
    }

    @NonNull @Override public Observable<PurchaseRestoreTotalResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> restorePurchasesAndClear(
            int requestCode)
    {
        return restorePurchases(requestCode)
                .finallyDo(() -> forgetRequestCode(requestCode));
    }

    @NonNull @Override public Observable<PurchaseRestoreTotalResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> restorePurchases(
            int requestCode)
    {
        PurchaseRestoreTotalResult<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType> result = new PurchaseRestoreTotalResult<>(requestCode);
        //noinspection Convert2Diamond
        return getPurchases(requestCode)
                .flatMap(fetchResult -> restorePurchase(requestCode, fetchResult.purchase)
                                .map(restoreResult -> new PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>(
                                        restoreResult.requestCode,
                                        restoreResult.purchase,
                                        null))
                                .onErrorResumeNext(error -> Observable.just(
                                        new PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>(
                                                requestCode,
                                                fetchResult.purchase,
                                                error)))
                )
                .doOnNext(result::add)
                .doOnNext(restored -> THToast.show("Restored purchase " + restored.purchase.getProductIdentifier()))
                .toList()
                .map(list -> result);
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        billingAvailableTesterHolder.onActivityResult(requestCode, resultCode, data);
        productIdentifierFetcherHolder.onActivityResult(requestCode, resultCode, data);
        inventoryFetcherHolder.onActivityResult(requestCode, resultCode, data);
        purchaseFetcherHolder.onActivityResult(requestCode, resultCode, data);
        purchaserHolder.onActivityResult(requestCode, resultCode, data);
    }
}
