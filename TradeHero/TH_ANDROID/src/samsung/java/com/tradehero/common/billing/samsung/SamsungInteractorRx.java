package com.androidth.general.common.billing.samsung;

import com.androidth.general.common.billing.BillingInteractorRx;
import com.androidth.general.common.billing.BillingLogicHolderRx;

public interface SamsungInteractorRx<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<
                SamsungSKUType,
                SamsungOrderIdType>,
        SamsungActorType extends BillingLogicHolderRx<
                SamsungSKUListKeyType,
                SamsungSKUType,
                SamsungSKUListType,
                SamsungProductDetailType,
                SamsungPurchaseOrderType,
                SamsungOrderIdType,
                SamsungPurchaseType>>
        extends BillingInteractorRx<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungProductDetailType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungActorType>
{
}
