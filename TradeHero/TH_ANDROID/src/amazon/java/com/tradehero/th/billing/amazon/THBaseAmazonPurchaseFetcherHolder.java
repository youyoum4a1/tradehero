package com.tradehero.th.billing.amazon;

import android.app.Activity;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseFetcherHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseAmazonPurchaseFetcherHolder
    extends BaseAmazonPurchaseFetcherHolder<
            AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase,
            THAmazonPurchaseFetcher,
            AmazonException>
    implements THAmazonPurchaseFetcherHolder
{
    @NotNull protected final Provider<Activity> activityProvider;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseFetcherHolder(
            @NotNull Provider<THAmazonPurchaseFetcher> thAmazonPurchaseFetcherProvider,
            @NotNull Provider<Activity> activityProvider)
    {
        super(thAmazonPurchaseFetcherProvider);
        this.activityProvider = activityProvider;
    }
    //</editor-fold>
}
