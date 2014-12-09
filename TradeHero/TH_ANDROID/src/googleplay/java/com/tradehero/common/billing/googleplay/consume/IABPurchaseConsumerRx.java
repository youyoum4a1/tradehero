package com.tradehero.common.billing.googleplay.consume;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.RequestCodeActor;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import rx.Observable;

public interface IABPurchaseConsumerRx<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends RequestCodeActor
{
    void onDestroy();

    @NonNull Observable<PurchaseConsumeResult<IABSKUType,
            IABOrderIdType,
            IABPurchaseType>> get();
}
