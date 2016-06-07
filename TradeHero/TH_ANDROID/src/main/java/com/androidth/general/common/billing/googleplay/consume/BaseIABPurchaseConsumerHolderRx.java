package com.androidth.general.common.billing.googleplay.consume;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseRequestCodeHolder;
import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.billing.googleplay.IABPurchase;
import com.androidth.general.common.billing.googleplay.IABSKU;
import rx.Observable;

abstract public class BaseIABPurchaseConsumerHolderRx<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends BaseRequestCodeHolder<IABPurchaseConsumerRx<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType>>
        implements IABPurchaseConsumerHolderRx<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseIABPurchaseConsumerHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseConsumeResult<IABSKUType, IABOrderIdType, IABPurchaseType>> get(
            int requestCode,
            @NonNull IABPurchaseType purchase)
    {
        IABPurchaseConsumerRx<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType> consumer = actors.get(requestCode);
        if (consumer == null)
        {
            consumer = createPurchaseConsumer(requestCode, purchase);
            actors.put(requestCode, consumer);
        }
        return consumer.get();
    }

    @NonNull abstract protected IABPurchaseConsumerRx<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType> createPurchaseConsumer(int requestCode, @NonNull IABPurchaseType purchase);
}
