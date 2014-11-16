package com.tradehero.common.billing.googleplay.consume;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.RequestCodeHolder;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import rx.Observable;

public interface IABPurchaseConsumerHolderRx<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends RequestCodeHolder
{
    @NonNull Observable<PurchaseConsumeResult<IABSKUType,
            IABOrderIdType,
            IABPurchaseType>> get(int requestCode, @NonNull IABPurchaseType purchase);
}
