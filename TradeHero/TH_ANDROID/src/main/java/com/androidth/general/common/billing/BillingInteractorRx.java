package com.androidth.general.common.billing;

import android.support.annotation.NonNull;
import com.androidth.general.common.activities.ActivityResultRequester;
import com.androidth.general.common.billing.identifier.ProductIdentifierListResult;
import com.androidth.general.common.billing.inventory.ProductInventoryResult;
import com.androidth.general.common.billing.purchase.PurchaseResult;
import com.androidth.general.common.billing.purchasefetch.PurchaseFetchResult;
import com.androidth.general.common.billing.restore.PurchaseRestoreResult;
import com.androidth.general.common.billing.restore.PurchaseRestoreTotalResult;
import com.androidth.general.common.billing.tester.BillingTestResult;
import java.util.List;
import rx.Observable;

public interface BillingInteractorRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<
                ProductIdentifierType,
                OrderIdType>,
        BillingLogicHolderType extends BillingLogicHolderRx<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType>>
        extends ActivityResultRequester
{
    @NonNull String getName();

    //<editor-fold desc="Test Billing">
    @NonNull Observable<BillingTestResult> testAndClear();

    @NonNull Observable<BillingTestResult> test();
    //</editor-fold>

    //<editor-fold desc="Get Product Identifiers">
    @NonNull Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> getIdsAndClear();

    @NonNull Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> getIds();
    //</editor-fold>

    //<editor-fold desc="Get Inventory">
    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventoryAndClear(
            @NonNull List<ProductIdentifierType> productIdentifiers);

    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventory(
            @NonNull List<ProductIdentifierType> productIdentifiers);

    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventoryAndClear();

    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventory();
    //</editor-fold>

    @NonNull Observable<PurchaseOrderType> createPurchaseOrder(
            @NonNull ProductDetailType detail);

    //<editor-fold desc="Get Purchases">
    @NonNull Observable<PurchaseFetchResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> getPurchasesAndClear();

    @NonNull Observable<PurchaseFetchResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> getPurchases();
    //</editor-fold>

    //<editor-fold desc="Purchase">
    @NonNull Observable<PurchaseResult<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> purchaseAndClear(@NonNull PurchaseOrderType purchaseOrder);

    @NonNull Observable<PurchaseResult<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> purchase(@NonNull PurchaseOrderType purchaseOrder);
    //</editor-fold>

    //<editor-fold desc="Restore Purchase">
    @NonNull Observable<PurchaseRestoreResult<ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> restorePurchaseAndClear(
            @NonNull ProductPurchaseType purchase);

    @NonNull Observable<PurchaseRestoreResult<ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> restorePurchase(
            @NonNull ProductPurchaseType purchase);

    @NonNull Observable<PurchaseRestoreTotalResult<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType>> restorePurchasesAndClear();

    @NonNull Observable<PurchaseRestoreTotalResult<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType>> restorePurchases();
    //</editor-fold>

    void manageSubscriptions();
}
