package com.ayondo.academy.billing.amazon.purchasefetch;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchasefetch.BaseAmazonPurchaseFetcherHolderRx;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.ayondo.academy.billing.amazon.ProcessingPurchase;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import javax.inject.Inject;

public class THBaseAmazonPurchaseFetcherHolderRx
    extends BaseAmazonPurchaseFetcherHolderRx<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase>
    implements THAmazonPurchaseFetcherHolderRx
{
    @NonNull protected final AmazonPurchasingService purchasingService;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseFetcherHolderRx(
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull @ProcessingPurchase StringSetPreference processingPurchaseStringSet)
    {
        super();
        this.purchasingService = purchasingService;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
    }
    //</editor-fold>

    @NonNull @Override protected THBaseAmazonPurchaseFetcherRx createFetcher(int requestCode)
    {
        return new THBaseAmazonPurchaseFetcherRx(requestCode, purchasingService, processingPurchaseStringSet);
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
