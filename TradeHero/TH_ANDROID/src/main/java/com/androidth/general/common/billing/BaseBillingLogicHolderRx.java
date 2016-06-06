package com.androidth.general.common.billing;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.identifier.ProductIdentifierFetcherHolderRx;
import com.androidth.general.common.billing.identifier.ProductIdentifierListResult;
import com.androidth.general.common.billing.inventory.BillingInventoryFetcherHolderRx;
import com.androidth.general.common.billing.inventory.ProductInventoryResult;
import com.androidth.general.common.billing.purchase.BillingPurchaserHolderRx;
import com.androidth.general.common.billing.purchase.PurchaseResult;
import com.androidth.general.common.billing.purchasefetch.BillingPurchaseFetcherHolderRx;
import com.androidth.general.common.billing.purchasefetch.PurchaseFetchResult;
import com.androidth.general.common.billing.restore.PurchaseRestoreResult;
import com.androidth.general.common.billing.restore.PurchaseRestoreResultWithError;
import com.androidth.general.common.billing.restore.PurchaseRestoreTotalResult;
import com.androidth.general.common.billing.tester.BillingAvailableTesterHolderRx;
import com.androidth.general.common.billing.tester.BillingTestResult;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.rx.ReplaceWithFunc1;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

abstract public class BaseBillingLogicHolderRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
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
    @NonNull protected final ProductDetailCacheRx<ProductIdentifierType, ProductDetailType> productDetailCache;
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
            @NonNull ProductDetailCacheRx<ProductIdentifierType, ProductDetailType> productDetailCache,
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
    @NonNull @Override public Observable<BillingTestResult> testAndClear(final int requestCode)
    {
        return test(requestCode)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        BaseBillingLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
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
            final int requestCode)
    {
        return getIds(requestCode)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        BaseBillingLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
    }

    @NonNull @Override public Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> getIds(
            final int requestCode)
    {
        return test(requestCode)
                .flatMap(
                        new Func1<BillingTestResult, Observable<? extends ProductIdentifierListResult<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType>>>()
                        {
                            @Override
                            public Observable<? extends ProductIdentifierListResult<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType>> call(
                                    BillingTestResult result)
                            {
                                return productIdentifierFetcherHolder.get(requestCode);
                            }
                        })
                .doOnNext(
                        new Action1<ProductIdentifierListResult<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType>>()
                        {
                            @Override public void call(
                                    ProductIdentifierListResult<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType> results)
                            {
                                for (Map.Entry<ProductIdentifierListKeyType, ProductIdentifierListType> entry : results.mappedIds.entrySet())
                                {
                                    productIdentifierCache.onNext(entry.getKey(), entry.getValue());
                                }
                            }
                        });
    }
    //</editor-fold>

    //<editor-fold desc="Get Inventory">
    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventoryAndClear(final int requestCode)
    {
        return getInventory(requestCode)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        BaseBillingLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
    }

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventory(final int requestCode)
    {
        return getIds(requestCode)
                .flatMap(
                        new Func1<ProductIdentifierListResult<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType>, Observable<? extends ProductInventoryResult<ProductIdentifierType, ProductDetailType>>>()
                        {
                            @Override public Observable<? extends ProductInventoryResult<ProductIdentifierType, ProductDetailType>> call(
                                    ProductIdentifierListResult<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType> results)
                            {
                                List<ProductIdentifierType> ids = new ArrayList<>();
                                for (ProductIdentifierListType value : results.mappedIds.values())
                                {
                                    ids.addAll(value);
                                }
                                return BaseBillingLogicHolderRx.this.getInventory(requestCode, ids);
                            }
                        });
    }

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventoryAndClear(final int requestCode, @NonNull List<ProductIdentifierType> productIdentifiers)
    {
        return getInventory(requestCode, productIdentifiers)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        BaseBillingLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
    }

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventory(final int requestCode, @NonNull final List<ProductIdentifierType> productIdentifiers)
    {
        return test(requestCode)
                .flatMap(new Func1<BillingTestResult, Observable<? extends ProductInventoryResult<ProductIdentifierType, ProductDetailType>>>()
                {
                    @Override public Observable<? extends ProductInventoryResult<ProductIdentifierType, ProductDetailType>> call(
                            BillingTestResult result)
                    {
                        return inventoryFetcherHolder.get(requestCode, productIdentifiers);
                    }
                })
                .doOnNext(new Action1<ProductInventoryResult<ProductIdentifierType, ProductDetailType>>()
                {
                    @Override public void call(
                            ProductInventoryResult<ProductIdentifierType, ProductDetailType> results)
                    {
                        for (Map.Entry<ProductIdentifierType, ProductDetailType> entry : results.mapped.entrySet())
                        {
                            productDetailCache.onNext(entry.getKey(), entry.getValue());
                        }
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Purchase">
    @NonNull @Override public Observable<PurchaseResult<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> purchaseAndClear(final int requestCode, @NonNull PurchaseOrderType purchaseOrder)
    {
        return purchase(requestCode, purchaseOrder)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        BaseBillingLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
    }

    @NonNull @Override public Observable<PurchaseResult<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> purchase(final int requestCode, @NonNull final PurchaseOrderType purchaseOrder)
    {
        return test(requestCode)
                .flatMap(
                        new Func1<BillingTestResult, Observable<? extends PurchaseResult<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseResult<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType>> call(
                                    BillingTestResult result)
                            {
                                return purchaserHolder.get(requestCode, purchaseOrder);
                            }
                        })
                .doOnNext(new Action1<PurchaseResult<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType>>()
                {
                    @Override public void call(
                            PurchaseResult<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType> result)
                    {
                        purchaseCache.onNext(result.purchase.getOrderId(), result.purchase);
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Purchases Fetch">
    @NonNull @Override public Observable<PurchaseFetchResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> getPurchasesAndClear(final int requestCode)
    {
        return getPurchases(requestCode)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        BaseBillingLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
    }

    @NonNull @Override public Observable<PurchaseFetchResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> getPurchases(final int requestCode)
    {
        return test(requestCode)
                .flatMap(
                        new Func1<BillingTestResult, Observable<? extends PurchaseFetchResult<ProductIdentifierType, OrderIdType, ProductPurchaseType>>>()
                        {
                            @Override public Observable<? extends PurchaseFetchResult<ProductIdentifierType, OrderIdType, ProductPurchaseType>> call(
                                    BillingTestResult result)
                            {
                                return purchaseFetcherHolder.get(requestCode);
                            }
                        })
                .doOnNext(new Action1<PurchaseFetchResult<ProductIdentifierType, OrderIdType, ProductPurchaseType>>()
                {
                    @Override public void call(
                            PurchaseFetchResult<ProductIdentifierType, OrderIdType, ProductPurchaseType> result)
                    {
                        purchaseCache.onNext(result.purchase.getOrderId(), result.purchase);
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Restore Purchases">
    @NonNull @Override public Observable<PurchaseRestoreResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> restorePurchaseAndClear(
            final int requestCode,
            @NonNull ProductPurchaseType purchase)
    {
        return restorePurchase(requestCode, purchase)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        BaseBillingLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
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
            final int requestCode)
    {
        return restorePurchases(requestCode)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        BaseBillingLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
    }

    @NonNull @Override public Observable<PurchaseRestoreTotalResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> restorePurchases(
            final int requestCode)
    {
        final PurchaseRestoreTotalResult<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType> result = new PurchaseRestoreTotalResult<>(requestCode);
        //noinspection Convert2Diamond
        return getPurchases(requestCode)
                .flatMap(
                        new Func1<PurchaseFetchResult<ProductIdentifierType, OrderIdType, ProductPurchaseType>, Observable<? extends PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>> call(
                                    final PurchaseFetchResult<ProductIdentifierType, OrderIdType, ProductPurchaseType> fetchResult)
                            {
                                return BaseBillingLogicHolderRx.this.restorePurchase(requestCode, fetchResult.purchase)
                                        .map(new Func1<PurchaseRestoreResult<ProductIdentifierType, OrderIdType, ProductPurchaseType>, PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>>()
                                        {
                                            @Override
                                            public PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType> call(
                                                    PurchaseRestoreResult<ProductIdentifierType, OrderIdType, ProductPurchaseType> restoreResult)
                                            {
                                                return new PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>(
                                                        restoreResult.requestCode,
                                                        restoreResult.purchase,
                                                        null);
                                            }
                                        })
                                        .onErrorResumeNext(
                                                new Func1<Throwable, Observable<? extends PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>>>()
                                                {
                                                    @Override
                                                    public Observable<? extends PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>> call(
                                                            Throwable error)
                                                    {
                                                        return Observable.just(
                                                                new PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>(
                                                                        requestCode,
                                                                        fetchResult.purchase,
                                                                        error));
                                                    }
                                                });
                            }
                        }
                )
                .doOnNext(new Action1<PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>>()
                {
                    @Override public void call(
                            PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType> restored)
                    {
                        result.add(restored);
                        THToast.show("Restored purchase " + restored.purchase.getProductIdentifier());
                    }
                })
                .toList()
                .map(new ReplaceWithFunc1<List<PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>>, PurchaseRestoreTotalResult<ProductIdentifierType, OrderIdType, ProductPurchaseType>>(
                        result));
    }
    //</editor-fold>

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        billingAvailableTesterHolder.onActivityResult(activity, requestCode, resultCode, data);
        productIdentifierFetcherHolder.onActivityResult(activity, requestCode, resultCode, data);
        inventoryFetcherHolder.onActivityResult(activity, requestCode, resultCode, data);
        purchaseFetcherHolder.onActivityResult(activity, requestCode, resultCode, data);
        purchaserHolder.onActivityResult(activity, requestCode, resultCode, data);
    }
}
