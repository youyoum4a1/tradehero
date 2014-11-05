package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaserHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;

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
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaserHolder(
            @NonNull Provider<THAmazonPurchaser> thAmazonPurchaserProvider)
    {
        super(thAmazonPurchaserProvider);
    }
    //</editor-fold>
}
