package com.androidth.general.common.billing.googleplay;

import com.androidth.general.common.billing.BillingInteractorRx;
import com.androidth.general.common.billing.BillingLogicHolderRx;

public interface IABInteractorRx<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<
                IABSKUType,
                IABOrderIdType>,
        IABActorType extends BillingLogicHolderRx<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType>>
        extends BillingInteractorRx<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType,
        IABProductDetailType,
        IABPurchaseOrderType,
        IABOrderIdType,
        IABPurchaseType,
        IABActorType>
{
}
