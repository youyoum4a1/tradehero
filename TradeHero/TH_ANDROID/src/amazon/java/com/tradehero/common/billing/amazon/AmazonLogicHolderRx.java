package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BillingLogicHolderRx;
import com.tradehero.common.billing.amazon.consume.PurchaseConsumedResult;
import rx.Observable;

public interface AmazonLogicHolderRx<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>,
        AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends
        BillingLogicHolderRx<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType,
                AmazonProductDetailType,
                AmazonPurchaseOrderType,
                AmazonOrderIdType,
                AmazonPurchaseType>
{
    @NonNull public Observable<PurchaseConsumedResult<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType>> consume(
            int requestCode,
            @NonNull AmazonPurchaseType purchase);
}
