package com.tradehero.th.billing;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseBillingInteractorRx;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.identifier.ProductIdentifierListResult;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.billing.purchasefetch.PurchaseFetchResult;
import com.tradehero.common.billing.restore.PurchaseRestoreResult;
import com.tradehero.common.billing.restore.PurchaseRestoreTotalResult;
import com.tradehero.common.billing.tester.BillingTestResult;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;
import com.tradehero.th.fragments.billing.ProductDetailView;
import com.tradehero.th.rx.ReplaceWith;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;
import rx.Observable;
import rx.functions.Func1;

abstract public class THBaseBillingInteractorRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<
                ProductIdentifierType,
                THOrderIdType>,
        THBillingLogicHolderType extends THBillingLogicHolderRx<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType>,
        ProductDetailViewType extends ProductDetailView<
                ProductIdentifierType,
                THProductDetailType>,
        ProductDetailAdapterType extends ProductDetailAdapter<
                ProductIdentifierType,
                THProductDetailType,
                ProductDetailViewType>>
        extends BaseBillingInteractorRx<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        THProductDetailType,
        THPurchaseOrderType,
        THOrderIdType,
        THProductPurchaseType,
        THBillingLogicHolderType>
        implements
        THBillingInteractorRx<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                THBillingLogicHolderType>
{
    public static final int ACTION_RESET_PORTFOLIO = 1;

    @NonNull protected final Provider<Activity> activityProvider;
    @NonNull protected final THBillingAlertDialogRxUtil<
            ProductIdentifierType,
            THProductDetailType,
            THBillingLogicHolderType,
            ProductDetailViewType,
            ProductDetailAdapterType,
            THOrderIdType,
            THProductPurchaseType> billingAlertDialogUtil;

    //<editor-fold desc="Constructors">
    protected THBaseBillingInteractorRx(
            @NonNull THBillingLogicHolderType billingLogicHolder,
            @NonNull Provider<Activity> activityProvider,
            @NonNull THBillingAlertDialogRxUtil<
                    ProductIdentifierType,
                    THProductDetailType,
                    THBillingLogicHolderType,
                    ProductDetailViewType,
                    ProductDetailAdapterType,
                    THOrderIdType,
                    THProductPurchaseType> billingAlertDialogUtil)
    {
        super(billingLogicHolder);
        this.activityProvider = activityProvider;
        this.billingAlertDialogUtil = billingAlertDialogUtil;
        this.billingAlertDialogUtil.setStoreName(getName());
    }
    //</editor-fold>

    @NonNull protected <T> Observable<T> popErrorAndHandle(@NonNull final Observable<T> observable)
    {
        return observable.onErrorResumeNext(new Func1<Throwable, Observable<? extends T>>()
        {
            @Override public Observable<? extends T> call(Throwable error)
            {
                return billingAlertDialogUtil.popErrorAndHandle(
                        activityProvider.get(),
                        error)
                        .flatMap(new ReplaceWith<OnDialogClickEvent, Observable<? extends T>>(Observable.<T>empty()));
            }
        });
    }

    //<editor-fold desc="Test Billing">
    @NonNull @Override public Observable<BillingTestResult> testAndClear()
    {
        return popErrorAndHandle(super.testAndClear());
    }

    @NonNull @Override public Observable<BillingTestResult> test()
    {
        return popErrorAndHandle(super.test());
    }
    //</editor-fold>

    //<editor-fold desc="Get Product Identifiers">
    @NonNull @Override public Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> getIdsAndClear()
    {
        return popErrorAndHandle(super.getIdsAndClear());
    }

    @NonNull @Override public Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> getIds()
    {
        return popErrorAndHandle(super.getIds());
    }
    //</editor-fold>

    //<editor-fold desc="Get Inventory">
    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            THProductDetailType>> getInventoryAndClear(
            @NonNull List<ProductIdentifierType> productIdentifiers)
    {
        return popErrorAndHandle(super.getInventoryAndClear(productIdentifiers));
    }

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            THProductDetailType>> getInventory(
            @NonNull List<ProductIdentifierType> productIdentifiers)
    {
        return popErrorAndHandle(super.getInventory(productIdentifiers));
    }

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            THProductDetailType>> getInventoryAndClear()
    {
        return popErrorAndHandle(super.getInventoryAndClear());
    }

    @NonNull @Override public Observable<ProductInventoryResult<
            ProductIdentifierType,
            THProductDetailType>> getInventory()
    {
        return popErrorAndHandle(super.getInventory());
    }
    //</editor-fold>

    //<editor-fold desc="Get Purchases">
    @NonNull @Override public Observable<PurchaseFetchResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> getPurchasesAndClear()
    {
        return popErrorAndHandle(super.getPurchasesAndClear());
    }

    @NonNull @Override public Observable<PurchaseFetchResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> getPurchases()
    {
        return popErrorAndHandle(super.getPurchases());
    }
    //</editor-fold>

    //<editor-fold desc="Purchase">
    @NonNull @Override public Observable<PurchaseResult<
            ProductIdentifierType,
            THPurchaseOrderType,
            THOrderIdType,
            THProductPurchaseType>> purchaseAndClear(
            @NonNull THPurchaseOrderType purchaseOrder)
    {
        return popErrorAndHandle(super.purchaseAndClear(purchaseOrder));
    }

    @NonNull @Override public Observable<PurchaseResult<
            ProductIdentifierType,
            THPurchaseOrderType,
            THOrderIdType,
            THProductPurchaseType>> purchase(
            @NonNull THPurchaseOrderType purchaseOrder)
    {
        return popErrorAndHandle(super.purchase(purchaseOrder));
    }

    @NonNull protected Observable<THProductDetailType> getDomainDialogResult(
            @NonNull final ProductIdentifierDomain domain)
    {
        return getInventory()
                .flatMap(
                        new Func1<ProductInventoryResult<ProductIdentifierType, THProductDetailType>, Observable<? extends THProductDetailType>>()
                        {
                            @Override public Observable<? extends THProductDetailType> call(
                                    ProductInventoryResult<ProductIdentifierType, THProductDetailType> result)
                            {
                                Map<ProductIdentifierType, THProductDetailType> filtered2 = new HashMap<>();
                                for (Map.Entry<ProductIdentifierType, THProductDetailType> entry : result.mapped.entrySet())
                                {
                                    if (entry.getValue().getDomain().equals(domain))
                                    {
                                        filtered2.put(entry.getKey(), entry.getValue());
                                    }
                                }
                                if (filtered2.size() > 0)
                                {
                                    return billingAlertDialogUtil.popBuyDialogAndHandle(
                                            activityProvider.get(),
                                            domain,
                                            new ProductInventoryResult<>(result.requestCode, filtered2),
                                            null);
                                }
                                return Observable.empty();
                            }
                        });
    }

    @NonNull @Override public Observable<PurchaseResult<
            ProductIdentifierType,
            THPurchaseOrderType,
            THOrderIdType,
            THProductPurchaseType>> purchaseAndClear(
            @NonNull ProductIdentifierDomain domain)
    {
        return popErrorAndHandle(getDomainDialogResult(domain)
                .flatMap(
                        new Func1<THProductDetailType, Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>> call(
                                    final THProductDetailType selected)
                            {
                                return THBaseBillingInteractorRx.this.createPurchaseOrder(selected)
                                        .flatMap(
                                                new Func1<THPurchaseOrderType, Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>>>()
                                                {
                                                    @Override
                                                    public Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>> call(
                                                            THPurchaseOrderType purchaseOrder)
                                                    {
                                                        return billingLogicHolder.purchaseAndClear(
                                                                billingLogicHolder.getUnusedRequestCode(),
                                                                purchaseOrder);
                                                    }
                                                });
                            }
                        }
                ));
    }

    @NonNull @Override public Observable<PurchaseResult<
            ProductIdentifierType,
            THPurchaseOrderType,
            THOrderIdType,
            THProductPurchaseType>> purchase(
            @NonNull ProductIdentifierDomain domain)
    {
        return popErrorAndHandle(getDomainDialogResult(domain)
                .flatMap(
                        new Func1<THProductDetailType, Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>> call(
                                    final THProductDetailType selected)
                            {
                                return THBaseBillingInteractorRx.this.createPurchaseOrder(selected)
                                        .flatMap(
                                                new Func1<THPurchaseOrderType, Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>>>()
                                                {
                                                    @Override
                                                    public Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>> call(
                                                            THPurchaseOrderType purchaseOrder)
                                                    {
                                                        return billingLogicHolder.purchase(
                                                                billingLogicHolder.getUnusedRequestCode(),
                                                                purchaseOrder);
                                                    }
                                                });
                            }
                        }
                ));
    }
    //</editor-fold>

    //<editor-fold desc="Premium Follow">
    @NonNull @Override
    public Observable<PurchaseResult<
            ProductIdentifierType,
            THPurchaseOrderType,
            THOrderIdType,
            THProductPurchaseType>> purchaseAndPremiumFollowAndClear(
            @NonNull final UserBaseKey heroId)
    {
        return popErrorAndHandle(getDomainDialogResult(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS)
                .flatMap(
                        new Func1<THProductDetailType, Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>> call(
                                    final THProductDetailType selected)
                            {
                                return THBaseBillingInteractorRx.this.createPurchaseOrder(selected, heroId)
                                        .flatMap(
                                                new Func1<THPurchaseOrderType, Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>>>()
                                                {
                                                    @Override
                                                    public Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>> call(
                                                            THPurchaseOrderType purchaseOrder)
                                                    {
                                                        return billingLogicHolder.purchaseAndClear(
                                                                billingLogicHolder.getUnusedRequestCode(),
                                                                purchaseOrder);
                                                    }
                                                });
                            }
                        }
                ));
    }

    @NonNull @Override
    public Observable<PurchaseResult<
            ProductIdentifierType,
            THPurchaseOrderType,
            THOrderIdType,
            THProductPurchaseType>> purchaseAndPremiumFollow(
            @NonNull final UserBaseKey heroId)
    {
        return popErrorAndHandle(getDomainDialogResult(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS)
                .flatMap(
                        new Func1<THProductDetailType, Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>> call(
                                    final THProductDetailType selected)
                            {
                                return THBaseBillingInteractorRx.this.createPurchaseOrder(selected, heroId)
                                        .flatMap(
                                                new Func1<THPurchaseOrderType, Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>>>()
                                                {
                                                    @Override
                                                    public Observable<? extends PurchaseResult<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType>> call(
                                                            THPurchaseOrderType purchaseOrder)
                                                    {
                                                        return billingLogicHolder.purchase(
                                                                billingLogicHolder.getUnusedRequestCode(),
                                                                purchaseOrder);
                                                    }
                                                });
                            }
                        }
                ));
    }
    //</editor-fold>

    //<editor-fold desc="Restore Purchases">
    @NonNull @Override public Observable<PurchaseRestoreResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> restorePurchaseAndClear(
            @NonNull THProductPurchaseType purchase)
    {
        return popErrorAndHandle(super.restorePurchaseAndClear(purchase));
    }

    @NonNull @Override public Observable<PurchaseRestoreResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> restorePurchase(
            @NonNull THProductPurchaseType purchase)
    {
        return popErrorAndHandle(super.restorePurchase(purchase));
    }

    @NonNull @Override public Observable<PurchaseRestoreTotalResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> restorePurchasesAndClear()
    {
        return restorePurchasesAndClear(true);
    }

    @NonNull public Observable<PurchaseRestoreTotalResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> restorePurchasesAndClear(final boolean fullReport)
    {
        return popErrorAndHandle(super.restorePurchasesAndClear()
                .flatMap(
                        new Func1<PurchaseRestoreTotalResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>, Observable<? extends PurchaseRestoreTotalResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseRestoreTotalResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>> call(
                                    PurchaseRestoreTotalResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType> result)
                            {
                                if (result.getCount() > 0 || fullReport)
                                {
                                    return billingAlertDialogUtil.popRestoreResultAndHandle(
                                            activityProvider.get(),
                                            result)
                                            .map(new ReplaceWith<OnDialogClickEvent, PurchaseRestoreTotalResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>>(
                                                    result));
                                }
                                return Observable.empty();
                            }
                        }));
    }

    @NonNull @Override public Observable<PurchaseRestoreTotalResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>> restorePurchases()
    {
        return popErrorAndHandle(super.restorePurchases()
                .flatMap(
                        new Func1<PurchaseRestoreTotalResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>, Observable<? extends PurchaseRestoreTotalResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>>>()
                        {
                            @Override
                            public Observable<? extends PurchaseRestoreTotalResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>> call(
                                    final PurchaseRestoreTotalResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType> result)
                            {
                                return billingAlertDialogUtil.popRestoreResultAndHandle(
                                        activityProvider.get(),
                                        result)
                                        .map(new ReplaceWith<OnDialogClickEvent, PurchaseRestoreTotalResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>>(result));
                            }
                        }));
    }
    //</editor-fold>
}
