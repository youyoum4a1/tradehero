package com.androidth.general.billing;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseBillingLogicHolderRx;
import com.androidth.general.common.billing.BaseProductIdentifierList;
import com.androidth.general.common.billing.ProductDetailCacheRx;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductIdentifierListCacheRx;
import com.androidth.general.common.billing.ProductIdentifierListKey;
import com.androidth.general.common.billing.ProductPurchaseCacheRx;
import com.androidth.general.common.billing.identifier.ProductIdentifierFetcherHolderRx;
import com.androidth.general.common.billing.inventory.BillingInventoryFetcherHolderRx;
import com.androidth.general.common.billing.inventory.ProductInventoryResult;
import com.androidth.general.common.billing.purchase.BillingPurchaserHolderRx;
import com.androidth.general.common.billing.purchase.PurchaseResult;
import com.androidth.general.common.billing.purchasefetch.BillingPurchaseFetcherHolderRx;
import com.androidth.general.common.billing.restore.PurchaseRestoreResult;
import com.androidth.general.common.billing.tester.BillingAvailableTesterHolderRx;
import com.androidth.general.billing.report.PurchaseReportResult;
import com.androidth.general.billing.report.THPurchaseReporterHolderRx;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

abstract public class THBaseBillingLogicHolderRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends
        BaseBillingLogicHolderRx<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
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
            @NonNull ProductDetailCacheRx<ProductIdentifierType, THProductDetailType> productDetailCache,
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
                .flatMap(
                        new Func1<PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>, Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>> call(
                                    final PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType> purchaseResult)
                            {
                                return THBaseBillingLogicHolderRx.this.report(
                                        purchaseResult.requestCode,
                                        purchaseResult.purchase)
                                        .map(new Func1<PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>, PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>>()
                                        {
                                            @Override
                                            public PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType> call(
                                                    PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType> reportResult)
                                            {
                                                return purchaseResult;
                                            }
                                        });
                            }
                        });
    }
    //</editor-fold>

    //<editor-fold desc="Report Purchase">
    @NonNull @Override public Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> reportAndClear(final int requestCode,
            @NonNull THProductPurchaseType purchase)
    {
        return report(requestCode, purchase)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        THBaseBillingLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
    }

    @NonNull @Override public Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> report(final int requestCode,
            @NonNull final THProductPurchaseType purchase)
    {
        return getInventory(requestCode, Collections.singletonList(purchase.getProductIdentifier()))
                .flatMap(
                        new Func1<ProductInventoryResult<ProductIdentifierType, THProductDetailType>, Observable<? extends PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>> call(
                                    ProductInventoryResult<ProductIdentifierType, THProductDetailType> result)
                            {
                                THProductDetailType detail = result.mapped.get(purchase.getProductIdentifier());
                                if (detail != null)
                                {
                                    return THBaseBillingLogicHolderRx.this.report(requestCode, purchase, detail);
                                }
                                return Observable.empty();
                            }
                        });
    }

    @NonNull @Override public Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> reportAndClear(final int requestCode,
            @NonNull THProductPurchaseType purchase, @NonNull THProductDetailType productDetail)
    {
        return report(requestCode, purchase, productDetail)
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        THBaseBillingLogicHolderRx.this.forgetRequestCode(requestCode);
                    }
                });
    }

    @NonNull @Override public Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> report(int requestCode,
            @NonNull THProductPurchaseType purchase, @NonNull THProductDetailType productDetail)
    {
        return purchaseReporterHolder.get(requestCode, purchase, productDetail);
    }
    //</editor-fold>

    //<editor-fold desc="Restore Purchase">
    @NonNull @Override public Observable<PurchaseRestoreResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> restorePurchase(
            final int requestCode,
            @NonNull THProductPurchaseType purchase)
    {
        return super.restorePurchase(requestCode, purchase)
                .flatMap(
                        new Func1<PurchaseRestoreResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>, Observable<? extends PurchaseRestoreResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseRestoreResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>> call(
                                    final PurchaseRestoreResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType> restored)
                            {
                                return THBaseBillingLogicHolderRx.this.report(requestCode, restored.purchase)
                                        .map(new Func1<PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>, PurchaseRestoreResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>>()
                                        {
                                            @Override public PurchaseRestoreResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType> call(
                                                    PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType> reported)
                                            {
                                                return restored;
                                            }
                                        });
                            }
                        });
    }
    //</editor-fold>

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            THProductDetailType>> getDetailsOfDomain(
            final int requestCode,
            @NonNull final ProductIdentifierDomain domain)
    {
        return getInventory(requestCode)
                .map(new Func1<ProductInventoryResult<ProductIdentifierType, THProductDetailType>,
                        ProductInventoryResult<ProductIdentifierType, THProductDetailType>>()
                {
                    @Override public ProductInventoryResult<ProductIdentifierType, THProductDetailType> call(
                            ProductInventoryResult<ProductIdentifierType, THProductDetailType> result)
                    {
                        Map<ProductIdentifierType, THProductDetailType> mapped = new HashMap<>();
                        for (Map.Entry<ProductIdentifierType, THProductDetailType> entry : result.mapped.entrySet())
                        {
                            if (entry.getValue().getDomain().equals(domain))
                            {
                                mapped.put(entry.getKey(), entry.getValue());
                            }
                        }
                        return new ProductInventoryResult<>(result.requestCode, mapped);
                    }
                });
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(activity, requestCode, resultCode, data);
        purchaseReporterHolder.onActivityResult(activity, requestCode, resultCode, data);
    }
}
