package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.BaseAmazonPurchaserHolder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

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
    @NotNull protected final Provider<Activity> activityProvider;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaserHolder(
            @NotNull Provider<THAmazonPurchaser> thAmazonPurchaserProvider,
            @NotNull Provider<Activity> activityProvider)
    {
        super(thAmazonPurchaserProvider);
        this.activityProvider = activityProvider;
    }
    //</editor-fold>
}
