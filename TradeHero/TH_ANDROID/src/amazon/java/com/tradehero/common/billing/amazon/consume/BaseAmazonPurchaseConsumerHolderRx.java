package com.tradehero.common.billing.amazon.consume;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseRequestCodeHolder;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import rx.Observable;

abstract public class BaseAmazonPurchaseConsumerHolderRx<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends BaseRequestCodeHolder<AmazonPurchaseConsumerRx<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType>>
        implements AmazonPurchaseConsumerHolderRx<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaseConsumerHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseConsumedResult<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType>> get(
            int requestCode,
            @NonNull AmazonPurchaseType purchase)
    {
        AmazonPurchaseConsumerRx<
                AmazonSKUType,
                AmazonOrderIdType,
                AmazonPurchaseType> actor = actors.get(requestCode);
        if (actor == null)
        {
            actor = createActor(requestCode, purchase);
            actors.put(requestCode, actor);
        }
        return actor.get();
    }

    @NonNull abstract protected AmazonPurchaseConsumerRx<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType> createActor(int requestCode, @NonNull AmazonPurchaseType purchase);
}
