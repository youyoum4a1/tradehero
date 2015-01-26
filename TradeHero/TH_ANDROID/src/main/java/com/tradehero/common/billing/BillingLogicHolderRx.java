package com.tradehero.common.billing;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.billing.identifier.ProductIdentifierListResult;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.billing.purchasefetch.PurchaseFetchResult;
import com.tradehero.common.billing.restore.PurchaseRestoreResult;
import com.tradehero.common.billing.restore.PurchaseRestoreTotalResult;
import com.tradehero.common.billing.tester.BillingTestResult;
import java.util.List;
import rx.Observable;

public interface BillingLogicHolderRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends RequestCodeHolder, ActivityResultRequester
{
    String getBillingHolderName(Resources resources);

    int getUnusedRequestCode();

    @NonNull Observable<BillingTestResult> testAndClear(int requestCode);

    @NonNull Observable<BillingTestResult> test(int requestCode);

    @NonNull Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> getIdsAndClear(int requestCode);

    @NonNull Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> getIds(int requestCode);

    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventoryAndClear(
            int requestCode,
            @NonNull List<ProductIdentifierType> productIdentifiers);

    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventory(
            int requestCode,
            @NonNull List<ProductIdentifierType> productIdentifiers);

    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventoryAndClear(
            int requestCode);

    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventory(
            int requestCode);

    @NonNull Observable<PurchaseResult<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> purchaseAndClear(int requestCode,
            @NonNull PurchaseOrderType purchaseOrder);

    @NonNull Observable<PurchaseResult<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> purchase(int requestCode,
            @NonNull PurchaseOrderType purchaseOrder);

    @NonNull Observable<PurchaseFetchResult<ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> getPurchasesAndClear(int requestCode);

    @NonNull Observable<PurchaseFetchResult<ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> getPurchases(int requestCode);

    @NonNull Observable<PurchaseRestoreResult<ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType>> restorePurchaseAndClear(int requestCode,
            @NonNull ProductPurchaseType purchase);

    @NonNull Observable<PurchaseRestoreResult<ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType>> restorePurchase(int requestCode,
            @NonNull ProductPurchaseType purchase);

    @NonNull Observable<PurchaseRestoreTotalResult<ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType>> restorePurchasesAndClear(int requestCode);

    @NonNull Observable<PurchaseRestoreTotalResult<ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType>> restorePurchases(int requestCode);
}
