package com.ayondo.academy.billing.amazon.purchase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchase.BaseAmazonPurchaserHolderRx;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.ayondo.academy.billing.amazon.ProcessingPurchase;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.billing.amazon.THAmazonPurchaseOrder;
import javax.inject.Inject;

public class THBaseAmazonPurchaserHolderRx
    extends BaseAmazonPurchaserHolderRx<
            AmazonSKU,
            THAmazonPurchaseOrder,
            THAmazonOrderId,
            THAmazonPurchase>
    implements THAmazonPurchaserHolderRx
{
    @NonNull protected final AmazonPurchasingService purchasingService;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaserHolderRx(
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull @ProcessingPurchase StringSetPreference processingPurchaseStringSet)
    {
        super();
        this.purchasingService = purchasingService;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
    }
    //</editor-fold>

    @NonNull @Override protected THBaseAmazonPurchaserRx createPurchaser(
            int requestCode,
            @NonNull THAmazonPurchaseOrder purchaseOrder)
    {
        return new THBaseAmazonPurchaserRx(
                requestCode,
                purchaseOrder,
                purchasingService,
                processingPurchaseStringSet);
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
