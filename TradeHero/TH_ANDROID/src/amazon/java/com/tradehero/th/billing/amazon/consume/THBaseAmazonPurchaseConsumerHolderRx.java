package com.tradehero.th.billing.amazon.consume;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.consume.AmazonPurchaseConsumerRx;
import com.tradehero.common.billing.amazon.consume.BaseAmazonPurchaseConsumerHolderRx;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.persistence.billing.THAmazonPurchaseCacheRx;
import javax.inject.Inject;

public class THBaseAmazonPurchaseConsumerHolderRx
        extends BaseAmazonPurchaseConsumerHolderRx<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase>
        implements THAmazonPurchaseConsumerHolderRx
{
    @NonNull protected final AmazonPurchasingService purchasingService;
    @NonNull protected final THAmazonPurchaseCacheRx thAmazonPurchaseCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseConsumerHolderRx(
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonPurchaseCacheRx thAmazonPurchaseCache)
    {
        super();
        this.purchasingService = purchasingService;
        this.thAmazonPurchaseCache = thAmazonPurchaseCache;
    }
    //</editor-fold>

    @NonNull @Override protected AmazonPurchaseConsumerRx<AmazonSKU, THAmazonOrderId, THAmazonPurchase> createActor(int requestCode,
            @NonNull THAmazonPurchase purchase)
    {
        return new THBaseAmazonPurchaseConsumerRx(
                requestCode,
                purchase,
                purchasingService,
                thAmazonPurchaseCache);
    }
}
