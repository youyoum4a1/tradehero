package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.BaseAmazonPurchaserHolder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.activities.CurrentActivityHolder;
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
    @NotNull protected final CurrentActivityHolder currentActivityHolder;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaserHolder(
            @NotNull Provider<THAmazonPurchaser> thAmazonPurchaserProvider,
            @NotNull CurrentActivityHolder currentActivityHolder)
    {
        super(thAmazonPurchaserProvider);
        this.currentActivityHolder = currentActivityHolder;
    }
    //</editor-fold>
}
