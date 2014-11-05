package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseConsumerHolder;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;

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
