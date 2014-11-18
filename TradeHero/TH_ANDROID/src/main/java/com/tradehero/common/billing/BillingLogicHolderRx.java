package com.tradehero.common.billing;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.identifier.ProductIdentifierListResult;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.billing.purchasefetch.PurchaseFetchResult;
import com.tradehero.common.billing.tester.BillingTestResult;
import com.tradehero.common.activities.ActivityResultRequester;
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

    @NonNull Observable<BillingTestResult> test(int requestCode);

    @NonNull Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> getIds(int requestCode);

    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventory(
            int requestCode,
            @NonNull List<ProductIdentifierType> productIdentifiers);

    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> getInventory(
            int requestCode);

    @NonNull Observable<PurchaseResult<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> purchase(int requestCode,
            @NonNull PurchaseOrderType purchaseOrder);

    @NonNull Observable<PurchaseFetchResult<ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> getPurchases(int requestCode);
}
