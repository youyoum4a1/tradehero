package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseConsumerHolder;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseAmazonPurchaseConsumerHolder
    extends BaseAmazonPurchaseConsumerHolder<
            AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase,
            THAmazonPurchaseConsumer>
    implements THAmazonPurchaseConsumerHolder
{
    @NotNull protected final Provider<THAmazonPurchaseConsumer> thAmazonPurchaseConsumerProvider;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseConsumerHolder(@NotNull Provider<THAmazonPurchaseConsumer> thAmazonPurchaseConsumerProvider)
    {
        super();
        this.thAmazonPurchaseConsumerProvider = thAmazonPurchaseConsumerProvider;
    }
    //</editor-fold>

    @Override protected THAmazonPurchaseConsumer createPurchaseConsumer()
    {
        return thAmazonPurchaseConsumerProvider.get();
    }
}
