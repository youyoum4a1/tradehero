package com.tradehero.common.billing;

import android.content.Intent;
import android.support.annotation.NonNull;

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

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        billingLogicHolder.onActivityResult(requestCode, resultCode, data);
    }
}
