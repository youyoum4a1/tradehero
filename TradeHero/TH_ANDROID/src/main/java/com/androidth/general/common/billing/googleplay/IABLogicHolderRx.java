package com.androidth.general.common.billing.googleplay;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BillingLogicHolderRx;
import com.androidth.general.common.billing.googleplay.consume.PurchaseConsumeResult;
import rx.Observable;

public interface IABLogicHolderRx<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends
        BillingLogicHolderRx<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType>
{
    @NonNull Observable<PurchaseConsumeResult<IABSKUType,
            IABOrderIdType,
            IABPurchaseType>> consumeAndClear(int requestCode, @NonNull IABPurchaseType purchase);

    @NonNull Observable<PurchaseConsumeResult<IABSKUType,
            IABOrderIdType,
            IABPurchaseType>> consume(int requestCode, @NonNull IABPurchaseType purchase);
}
