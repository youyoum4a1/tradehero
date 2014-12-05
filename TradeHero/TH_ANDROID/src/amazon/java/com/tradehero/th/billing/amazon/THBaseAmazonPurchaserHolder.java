package com.tradehero.th.billing.amazon;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaserHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import javax.inject.Inject;

public class THBaseAmazonPurchaserHolder
    extends BaseAmazonPurchaserHolder<
        AmazonSKU,
        THAmazonPurchaseOrder,
        THAmazonOrderId,
        THAmazonPurchase,
        THAmazonPurchaser,
        AmazonException>
    implements THAmazonPurchaserHolder
{
    @NonNull protected final AmazonPurchasingService purchasingService;
    @NonNull protected final THAmazonExceptionFactory amazonExceptionFactory;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaserHolder(
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonExceptionFactory amazonExceptionFactory,
            @NonNull @ProcessingPurchase StringSetPreference processingPurchaseStringSet)
    {
        super();
        this.purchasingService = purchasingService;
        this.amazonExceptionFactory = amazonExceptionFactory;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }

    @NonNull @Override protected THAmazonPurchaser createPurchaser(int requestCode)
    {
        return new THBaseAmazonPurchaser(
                requestCode,
                purchasingService,
                amazonExceptionFactory,
                processingPurchaseStringSet);
    }
}
