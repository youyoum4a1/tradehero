package com.tradehero.common.billing;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import java.util.List;

abstract public class BaseBillingInteractorRx<
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
    implements BillingInteractorRx<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingLogicHolderType>
{
    @NonNull protected final BillingLogicHolderType billingLogicHolder;

    //<editor-fold desc="Constructors">
    public BaseBillingInteractorRx(@NonNull BillingLogicHolderType billingLogicHolder)
    {
        super();
        this.billingLogicHolder = billingLogicHolder;
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    public void onDestroy()
    {
        billingLogicHolder.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="Request Code Management">
    @Override public int getUnusedRequestCode()
    {
        return billingLogicHolder.getUnusedRequestCode();
    }

    public boolean isUnusedRequestCode(int requestCode)
    {
        return billingLogicHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        billingLogicHolder.forgetRequestCode(requestCode);
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        billingLogicHolder.onActivityResult(requestCode, resultCode, data);
    }

    //<editor-fold desc="Billing Available">
    abstract protected AlertDialog popBillingUnavailable(Throwable billingException);
    //</editor-fold>

    //<editor-fold desc="Product Identifier Fetch">
    abstract protected AlertDialog popFetchProductIdentifiersFailed(int requestCode, Throwable exception);
    //</editor-fold>

    //<editor-fold desc="Inventory Fetch">
    abstract protected AlertDialog popInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, Throwable exception);
    //</editor-fold>

    //<editor-fold desc="Purchases Fetch">
    abstract protected AlertDialog popFetchPurchasesFailed(int requestCode, Throwable exception);
    //</editor-fold>

    //<editor-fold desc="Purchases Restore">
    //</editor-fold>

    //<editor-fold desc="Purchase">
    abstract protected AlertDialog popPurchaseFailed(
            int requestCode,
            PurchaseOrderType purchaseOrder,
            Throwable billingException,
            AlertDialog.OnClickListener restoreClickListener);
    //</editor-fold>
}
