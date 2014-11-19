package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseConsumerHolder;
import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseAmazonPurchaseConsumerHolder
    extends BaseAmazonPurchaseConsumerHolder<
            AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase,
            THAmazonPurchaseConsumer>
    implements THAmazonPurchaseConsumerHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseConsumerHolder(@NonNull Provider<THAmazonPurchaseConsumer> thAmazonPurchaseConsumerProvider)
    {
        super(thAmazonPurchaseConsumerProvider);
    }
    //</editor-fold>
}
