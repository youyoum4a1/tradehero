package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseConsumerHolder;
import com.tradehero.common.billing.googleplay.IABSKU;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseIABPurchaseConsumerHolder
    extends BaseIABPurchaseConsumerHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THIABPurchaseConsumer>
    implements THIABPurchaseConsumerHolder
{
    @NotNull protected final Provider<THIABPurchaseConsumer> thiabPurchaseConsumerProvider;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseConsumerHolder(@NotNull Provider<THIABPurchaseConsumer> thiabPurchaseConsumerProvider)
    {
        super();
        this.thiabPurchaseConsumerProvider = thiabPurchaseConsumerProvider;
    }
    //</editor-fold>

    @Override protected THIABPurchaseConsumer createPurchaseConsumer()
    {
        return thiabPurchaseConsumerProvider.get();
    }
}
