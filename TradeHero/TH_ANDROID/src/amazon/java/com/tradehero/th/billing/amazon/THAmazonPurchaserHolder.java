package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonPurchaserHolder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THPurchaserHolder;

public interface THAmazonPurchaserHolder
        extends
        AmazonPurchaserHolder<
                AmazonSKU,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase,
                AmazonException>,
        THPurchaserHolder<
                AmazonSKU,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase,
                AmazonException>
{
}
