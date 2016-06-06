package com.androidth.general.common.billing.googleplay.consume;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.RequestCodeActor;
import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.billing.googleplay.IABPurchase;
import com.androidth.general.common.billing.googleplay.IABSKU;
import rx.Observable;

public interface IABPurchaseConsumerRx<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends RequestCodeActor
{
    @NonNull Observable<PurchaseConsumeResult<IABSKUType,
            IABOrderIdType,
            IABPurchaseType>> get();
}
