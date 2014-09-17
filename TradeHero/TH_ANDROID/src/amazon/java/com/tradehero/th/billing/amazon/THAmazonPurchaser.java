package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonPurchaser;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THPurchaser;

public interface THAmazonPurchaser
        extends
        AmazonPurchaser<
                        AmazonSKU,
                        THAmazonPurchaseOrder,
                        THAmazonOrderId,
                        THAmazonPurchase,
                        AmazonException>,
        THPurchaser<
                AmazonSKU,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase,
                AmazonException>
{
}
