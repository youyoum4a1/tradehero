package com.tradehero.common.billing.amazon.consume;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.RequestCodeHolder;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import rx.Observable;

public interface AmazonPurchaseConsumerHolderRx<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
    extends RequestCodeHolder
{
    @NonNull Observable<PurchaseConsumedResult<AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType>> get(int requestCode, @NonNull AmazonPurchaseType purchase);
}
