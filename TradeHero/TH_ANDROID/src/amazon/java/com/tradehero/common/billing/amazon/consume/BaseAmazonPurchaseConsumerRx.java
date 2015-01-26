package com.tradehero.common.billing.amazon.consume;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.ProductType;
import com.tradehero.common.billing.BaseRequestCodeActor;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonPurchaseCacheRx;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import rx.Observable;

abstract public class BaseAmazonPurchaseConsumerRx<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends BaseRequestCodeActor
        implements AmazonPurchaseConsumerRx<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType>
{
    @NonNull protected AmazonPurchaseType purchase;
    @NonNull protected AmazonPurchasingService purchasingService;

    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaseConsumerRx(
            int requestCode,
            @NonNull AmazonPurchaseType purchase,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(requestCode);
        this.purchase = purchase;
        this.purchasingService = purchasingService;
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseConsumedResult<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType>> get()
    {
        if (purchase.getOrderId().receipt.getProductType().equals(ProductType.CONSUMABLE))
        {
            purchasingService.notifyFulfillment(purchase.getOrderId().receipt.getReceiptId(), FulfillmentResult.FULFILLED);
        }
        getPurchaseCache().invalidate(purchase.getOrderId());
        return Observable.just(createResult());
    }

    @NonNull abstract protected AmazonPurchaseCacheRx<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType> getPurchaseCache();

    protected void consumeAndInformSubject(@NonNull AmazonPurchasingService purchasingService)
    {
    }

    @NonNull PurchaseConsumedResult<AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType> createResult()
    {
        return new PurchaseConsumedResult<>(getRequestCode(), purchase);
    }
}
