package com.tradehero.common.billing.amazon.consume;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonActor;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import rx.Observable;

public interface AmazonPurchaseConsumerRx<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends AmazonActor
{
    @NonNull Observable<PurchaseConsumedResult<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType>> get();
}
